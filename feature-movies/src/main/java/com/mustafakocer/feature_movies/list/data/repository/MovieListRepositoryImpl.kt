// feature-movies/src/main/java/com/mustafakocer/feature_movies/list/data/repository/MovieListRepositoryImpl.kt
package com.mustafakocer.feature_movies.list.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.mustafakocer.core_database.pagination.PaginationSettings
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListRemoteKeyDao
import com.mustafakocer.feature_movies.list.data.mapper.toDomain
import com.mustafakocer.feature_movies.list.data.mediator.MovieListRemoteMediatorFactory
import com.mustafakocer.feature_movies.list.domain.model.MovieCategory
import com.mustafakocer.feature_movies.list.domain.model.MovieListItem
import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

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
@Singleton
class MovieListRepositoryImpl @Inject constructor(
    private val mediatorFactory: MovieListRemoteMediatorFactory,
    private val movieListDao: MovieListDao,
    private val remoteKeyDao: MovieListRemoteKeyDao,
) : MovieListRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getMoviesForCategory(category: MovieCategory): Flow<PagingData<MovieListItem>> {

        // Use simple pagination settings
        val paginationSettings = PaginationSettings.movieList

        return Pager(
            config = paginationSettings.toPagingConfig(),
            remoteMediator = mediatorFactory.create(category),
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