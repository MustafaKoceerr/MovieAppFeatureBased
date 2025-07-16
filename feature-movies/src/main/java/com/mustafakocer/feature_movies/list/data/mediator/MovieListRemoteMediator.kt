package com.mustafakocer.feature_movies.list.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.core_database.pagination.RemoteKey
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.shared.data.mapper.toEntityList
import kotlinx.coroutines.delay
import retrofit2.HttpException
import android.util.Log

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
    private val remoteKeyDao: RemoteKeyDao,
    private val database: androidx.room.RoomDatabase,
    private val networkConnectivityMonitor: NetworkConnectivityMonitor, // ✅ ADDED!
    private val category: MovieCategory,
) : RemoteMediator<Int, MovieListEntity>() {

    companion object {
        private const val STARTING_PAGE = 1
        private const val NETWORK_DELAY_MS = 300L
    }

    override suspend fun initialize(): InitializeAction {
        Log.d("RemoteMediator", "🔧 Initialize called")
        val hasCachedData = movieListDao.hasCachedDataForCategory(category.apiEndpoint)
        Log.d("RemoteMediator", "🔧 Has cached data: $hasCachedData")

        return if (hasCachedData) {
            Log.d("RemoteMediator", "🔧 SKIP_INITIAL_REFRESH - Cache var")
            InitializeAction.SKIP_INITIAL_REFRESH // ❌ Bu Page 1'i atlıyor!
        } else {
            Log.d("RemoteMediator", "🔧 LAUNCH_INITIAL_REFRESH - Cache yok")
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
        // Silme ve ekleme işlemi için de aynı query'yi kullanıyoruz.
        val queryKey = RemoteKey.createCompositeKey("movies", category.apiEndpoint)

        // ✅ CHECK CONNECTIVITY - RETURN ERROR FOR RETRY BUTTON!
        val connectionState = networkConnectivityMonitor.getCurrentConnectionState()
        if (!connectionState.isConnected) {
            // Return ERROR so UI shows retry button
            return MediatorResult.Error(AppException.NetworkException.NoInternetConnection())
        }

        return try {
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
                page = page
            )
            // 2. ADIM: Zarfın sağlam geldiğinden emin oluyoruz.
            if (!apiResponse.isSuccessful) {
                throw HttpException(apiResponse)
            }

            // 3. ADIM: Zarfı açıp içinden "mektubu" (body) alıyoruz.
            val body = apiResponse.body()
            if (body == null) {
                // Başarılı ama boş bir yanıt geldiyse, bu sayfanın sonu demektir.
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            // 4. ADIM: Artık mektubun içindeki alanlara güvenle erişebiliriz.
            val movies = body.results ?: emptyList()
            val endOfPaginationReached = movies.isEmpty() ||
                    (page >= body.totalPages)
            // Database transaction öncesi
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieListDao.deleteMoviesForCategory(category.apiEndpoint)
                    // 2. ADIM: Silme işlemi için oluşturduğumuz anahtarı kullan.
                    remoteKeyDao.deleteRemoteKey(queryKey)
                }

                val remoteKey = RemoteKey.create(
                    query = queryKey,
                    currentPage = page,
                    totalPages = body.totalPages,
                    totalItems = body.totalResults
                )
                remoteKeyDao.upsert(remoteKey)

                // 5. ADIM: DTO listesini Entity listesine çeviriyoruz.
                val movieEntities = movies.toEntityList(
                    category = category.apiEndpoint,
                    page = page,
                    pageSize = state.config.pageSize
                )
                movieListDao.upsertAll(movieEntities) // upsertAll, bir List bekler.
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
                remoteKeyDao.getRemoteKey(category.cacheKey)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MovieListEntity>,
    ): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            remoteKeyDao.getRemoteKey(category.cacheKey)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, MovieListEntity>,
    ): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            remoteKeyDao.getRemoteKey(category.cacheKey)
        }
    }
}