package com.mustafakocer.feature_movies.list.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.core_database.pagination.PaginationSettings
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.list.data.mediator.MovieListRemoteMediatorFactory
import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import com.mustafakocer.feature_movies.shared.data.mapper.toDomainList
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implements the [MovieListRepository] interface, providing a concrete data-handling strategy
 * for paginated movie lists using Jetpack Paging 3 with a [RemoteMediator].
 *
 * Architectural Decision: This repository uses the `RemoteMediator` pattern, which is ideal for
 * displaying data from a local cache (Single Source of Truth) while fetching more data from the
 * network in the background when the user scrolls to the end of the cached data. This provides
 * excellent offline support and a smooth user experience.
 */
@Singleton
class MovieListRepositoryImpl @Inject constructor(
    private val mediatorFactory: MovieListRemoteMediatorFactory,
    private val movieListDao: MovieListDao,
    private val remoteKeyDao: RemoteKeyDao,
) : MovieListRepository {

    /**
     * Retrieves a stream of paginated movie data for a specific category and language.
     *
     * @param category The category of movies to fetch.
     * @param language The language code for the API request.
     * @return A Flow of [PagingData] containing the movie list items.
     */
    @OptIn(ExperimentalPagingApi::class)
    override fun getMoviesByCategory(
        category: MovieCategory,
        language: String,
    ): Flow<PagingData<MovieListItem>> {
        val paginationSettings = PaginationSettings.forCatalog

        // Architectural Decision: The `Pager` is the main entry point for the Paging 3 library.
        // It is configured with three key components:
        return Pager(
            config = paginationSettings.toPagingConfig(),
            // 1. `remoteMediator`: An instance of `MovieListRemoteMediator` (created by our factory)
            //    is responsible for fetching data from the network when the local data is exhausted
            //    and saving the new data into the local database. The factory pattern is used here
            //    to allow passing runtime parameters (category, language) to the mediator.
            remoteMediator = mediatorFactory.create(category, language),
            // 2. `pagingSourceFactory`: This lambda provides the `PagingSource`. The `PagingSource`
            //    (from `movieListDao`) is the "Single Source of Truth" for the UI. It reads data
            //    directly from the database.
            pagingSourceFactory = {
                movieListDao.getMoviesForCategory(category.apiEndpoint, language)
            }
        ).flow.map { pagingData ->
            // 3. The final `map` operation transforms the `PagingData` stream from database
            //    entities (`MovieListEntity`) to domain models (`MovieListItem`). This ensures
            //    that the upper layers of the application remain decoupled from the data layer's
            //    specific entity implementations, adhering to Clean Architecture principles.
            pagingData.map { entity -> entity.toDomainList() }
        }
    }

    /**
     * Clears the cache for a specific category and language to force a full refresh.
     *
     * @param category The movie category to refresh.
     * @param language The language of the cache to clear.
     */
    override suspend fun refreshCategory(category: MovieCategory, language: String) {
        // Architectural Decision: To ensure a complete refresh, we must delete both the movie
        // data itself (`movieListDao`) and its associated pagination metadata (`remoteKeyDao`).
        // Deleting only the movies might not be sufficient if the RemoteKey still indicates
        // that the end of the list has been reached. Clearing the key forces the RemoteMediator
        // to restart the pagination from the very first page on the next data request.
        movieListDao.deleteMoviesForCategory(category.apiEndpoint, language)
        remoteKeyDao.deleteRemoteKey(category.cacheKey, language)
    }
}