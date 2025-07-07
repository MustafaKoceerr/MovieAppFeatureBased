package com.mustafakocer.feature_movies.list.data.mediator
// feature-movies/src/main/java/com/mustafakocer/feature_movies/list/data/mediator/MovieListRemoteMediator.kt

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_database.cache.CacheMetadata
import com.mustafakocer.core_database.pagination.RemoteKey
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListRemoteKeyDao
import com.mustafakocer.feature_movies.list.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.list.data.mapper.toEntity
import com.mustafakocer.feature_movies.list.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import kotlinx.coroutines.delay


/**
 * Remote Mediator for Movie List pagination
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Source Coordination
 * RESPONSIBILITY: Coordinate between remote API and local database
 *
 * PAGING 3 PATTERN:
 * - Handles network requests when cached data is exhausted
 * - Manages database transactions for atomic operations
 * - Uses core database RemoteKey for pagination state
 */
@OptIn(ExperimentalPagingApi::class)
class MovieListRemoteMediator(
    private val apiService: MovieApiService, // ← UPDATED: Single service
    private val movieListDao: MovieListDao,
    private val remoteKeyDao: MovieListRemoteKeyDao,
    private val database: androidx.room.RoomDatabase,
    private val networkConnectivityMonitor: NetworkConnectivityMonitor, // ✅ ADDED!
    private val category: MovieCategory,
    private val apiKey: String,
) : RemoteMediator<Int, MovieListEntity>() {

    companion object {
        private const val STARTING_PAGE = 1
        private const val NETWORK_DELAY_MS = 300L
    }

    override suspend fun initialize(): InitializeAction {
        return if (movieListDao.hasCachedDataForCategory(category.apiEndpoint)) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    /**
     * RemoteMediator Gibi Özel Durumlar: Paging 3'ün RemoteMediator'ı gibi senaryolarda, "bağlantı yok" durumu bir hata değil,
     * "cache'den devam et" anlamına gelebilir. Bu durumda, MediatorResult.Error döndürmek yerine
     * MediatorResult.Success(endOfPaginationReached = true) döndürmek daha doğru bir davranıştır. Yani kontrolün sonucu,
     * her zaman bir hata fırlatmak anlamına gelmez.
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieListEntity>,
    ): MediatorResult {

        return try {

            // ✅ CHECK CONNECTIVITY - RETURN ERROR FOR RETRY BUTTON!
            val connectionState = networkConnectivityMonitor.getCurrentConnectionState()
            if (!connectionState.isConnected) {
                // Return ERROR so UI shows retry button
                return MediatorResult.Error(AppException.NetworkException.NoInternetConnection())
            }

            delay(NETWORK_DELAY_MS)

            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKey?.currentPage ?: STARTING_PAGE
                }

                LoadType.PREPEND -> {
                    val remoteKey = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKey?.prevKey?.toIntOrNull()
                    if (prevPage == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    prevPage
                }

                LoadType.APPEND -> {
                    val remoteKey = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKey?.nextKey?.toIntOrNull()
                    if (nextPage == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    nextPage
                }
            }

            val apiResponse = apiService.getMoviesByCategory(
                category = category.apiEndpoint,
                apiKey = apiKey,
                page = page
            )

            val movies = apiResponse.results
            val endOfPaginationReached = movies.isEmpty() || page >= apiResponse.totalPages

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieListDao.deleteMoviesForCategory(category.apiEndpoint)
                    remoteKeyDao.deleteRemoteKeyForCategory(category.cacheKey)
                }

                val prevKey = if (page == STARTING_PAGE) null else (page - 1).toString()
                val nextKey = if (endOfPaginationReached) null else (page + 1).toString()

                val remoteKey = RemoteKey(
                    query = category.cacheKey,
                    entityType = "movies",
                    currentPage = page,
                    nextKey = nextKey,
                    prevKey = prevKey,
                    totalPages = apiResponse.totalPages,
                    totalItems = apiResponse.totalResults,
                    cache = CacheMetadata(
                        cachedAt = System.currentTimeMillis(),
                        expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
                        cacheVersion = 1,
                        isPersistent = false
                    )
                )
                remoteKeyDao.upsert(remoteKey)

                val movieEntities = movies.toEntity(category.apiEndpoint, page)
                movieListDao.upsertAll(movieEntities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    // ==================== HELPER METHODS ====================

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MovieListEntity>,
    ): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let {
                remoteKeyDao.getRemoteKeyForCategory(category.cacheKey)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MovieListEntity>,
    ): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            remoteKeyDao.getRemoteKeyForCategory(category.cacheKey)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, MovieListEntity>,
    ): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            remoteKeyDao.getRemoteKeyForCategory(category.cacheKey)
        }
    }
}