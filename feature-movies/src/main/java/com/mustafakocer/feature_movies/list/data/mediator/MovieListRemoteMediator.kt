package com.mustafakocer.feature_movies.list.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.core_database.pagination.RemoteKey
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.shared.data.mapper.toEntityList
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import retrofit2.HttpException
import java.io.IOException

/**
 * A [RemoteMediator] that coordinates between the local database cache and the remote API
 * for paginated movie lists.
 *
 * Architectural Decision: This class is the core of the "offline-first" pagination strategy for
 * movie lists. It is responsible for:
 * 1.  Triggering network requests to fetch new pages of data when the user scrolls beyond the
 *     data available in the local database.
 * 2.  Storing the fetched movies and the corresponding pagination keys (next/previous page)
 *     in the local database.
 * 3.  Handling atomic database transactions to ensure data consistency during cache updates.
 * This approach provides a seamless user experience with offline support, as the UI always
 * reads from the database, which is continuously updated by this mediator.
 */
@OptIn(ExperimentalPagingApi::class)
class MovieListRemoteMediator(
    private val apiService: MovieApiService,
    private val movieListDao: MovieListDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val database: RoomDatabase,
    private val category: MovieCategory,
    private val language: String,
) : RemoteMediator<Int, MovieListEntity>() {

    companion object {
        private const val STARTING_PAGE = 1
        private const val NETWORK_DELAY_MS = 300L
    }

    /**
     * Determines whether to trigger a remote refresh when Paging starts.
     */
    override suspend fun initialize(): InitializeAction {
        val hasCachedData = movieListDao.hasCachedDataForCategory(category.apiEndpoint, language)
        return if (hasCachedData) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    /**
     * Called by the Paging library to fetch more data from the network when needed.
     *
     * @param loadType The type of load operation (REFRESH, APPEND, or PREPEND).
     * @param state The current state of the pagination, including loaded pages and config.
     * @return A [MediatorResult] indicating success (with end-of-pagination signal) or an error.
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieListEntity>,
    ): MediatorResult {
        // We use the same query for both delete and insert operations.
        val queryKey = RemoteKey.createCompositeKey("movies", category.apiEndpoint)

        return try {
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

            // Fetch data from the API.
            val apiResponse = apiService.getMoviesByCategory(
                category = category.apiEndpoint,
                page = page,
            )

            if (!apiResponse.isSuccessful) throw HttpException(apiResponse)
            val body =
                apiResponse.body() ?: return MediatorResult.Success(endOfPaginationReached = true)

            val movies = body.results ?: emptyList()
            val endOfPaginationReached = movies.isEmpty() || (page >= body.totalPages)

            // Architectural Decision: `withTransaction` ensures atomicity. All database operations
            // within this block (clearing old data, inserting new data, updating remote keys)
            // either complete successfully together or are all rolled back if an error occurs.
            // This prevents the database from being left in an inconsistent state.
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieListDao.deleteMoviesForCategory(category.apiEndpoint, language)
                    remoteKeyDao.deleteRemoteKey(category.cacheKey, language)
                }

                val remoteKey = RemoteKey.create(
                    query = category.cacheKey,
                    language = language,
                    currentPage = page,
                    totalPages = body.totalPages,
                    totalItems = body.totalResults
                )
                remoteKeyDao.upsert(remoteKey)

                val movieEntities = movies.toEntityList(
                    category = category.apiEndpoint,
                    page = page,
                    pageSize = state.config.pageSize,
                    language = language
                )
                movieListDao.upsertAll(movieEntities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            // IOException represents network errors.
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            // HttpException represents non-2xx server responses.
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    // --- HELPER METHODS ---

    /**
     * Retrieves the [RemoteKey] for the last item in the loaded pages.
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MovieListEntity>,
    ): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let {
                remoteKeyDao.getRemoteKey(category.cacheKey, language)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MovieListEntity>,
    ): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            remoteKeyDao.getRemoteKey(category.cacheKey, language)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, MovieListEntity>,
    ): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            remoteKeyDao.getRemoteKey(category.cacheKey, language)
        }
    }
}