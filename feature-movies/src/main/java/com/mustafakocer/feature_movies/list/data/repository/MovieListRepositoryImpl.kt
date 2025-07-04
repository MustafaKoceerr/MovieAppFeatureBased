package com.mustafakocer.feature_movies.list.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomDatabase
import com.mustafakocer.core_database.pagination.PaginationSettings
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListRemoteKeyDao
import com.mustafakocer.feature_movies.list.data.mapper.toDomain
import com.mustafakocer.feature_movies.list.data.mediator.MovieListRemoteMediator
import com.mustafakocer.feature_movies.list.data.remote.MovieListApiService
import com.mustafakocer.feature_movies.list.domain.model.MovieCategory
import com.mustafakocer.feature_movies.list.domain.model.MovieListItem
import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Movie list repository implementation
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Repository Implementation
 * RESPONSIBILITY: Coordinate data sources for movie list operations
 *
 * PAGING 3 INTEGRATION:
 * - Uses core database PaginationSettings
 * - Uses Pager with RemoteMediator for network + database coordination
 * - Provides Flow<PagingData> for reactive UI updates
 * - Leverages core database cache management
 */
class MovieListRepositoryImpl @Inject constructor(
    private val apiService: MovieListApiService,
    private val movieListDao: MovieListDao,
    private val remoteKeyDao: MovieListRemoteKeyDao,
    private val database: RoomDatabase,
) : MovieListRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getMoviesForCategory(category: MovieCategory): Flow<PagingData<MovieListItem>> {

        // use core database pagination settings optimized for catalog browsing
        val paginationSettings = PaginationSettings.forUseCase(
            PaginationSettings.PaginationUseCase.CATALOG_BROWSE
        )

        return Pager(
            config = paginationSettings.toPagingConfig(),
            remoteMediator = MovieListRemoteMediator(
                apiService = apiService,
                movieListDao = movieListDao,
                remoteKeyDao = remoteKeyDao,
                database = database,
                category = category
            ),
            pagingSourceFactory = {
                movieListDao.getMoviesPagingSource(category.apiEndpoint)
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun refreshCategory(category: MovieCategory) {
        movieListDao.deleteMoviesForCategory(category.apiEndpoint)
        remoteKeyDao.deleteRemoteKeyForCategory(category.cacheKey)
    }

    override suspend fun clearCacheForCategory(category: MovieCategory) {
        movieListDao.deleteMoviesForCategory(category.apiEndpoint)
        remoteKeyDao.deleteRemoteKeyForCategory(category.cacheKey)
    }

    override suspend fun hasCachedData(category: MovieCategory): Boolean {
        return movieListDao.hasCachedDataForCategory(category.apiEndpoint)
    }

}