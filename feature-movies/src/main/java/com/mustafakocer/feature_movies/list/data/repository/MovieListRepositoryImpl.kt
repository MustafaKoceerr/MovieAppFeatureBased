package com.mustafakocer.feature_movies.list.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.core_database.pagination.PaginationSettings
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.list.data.mediator.MovieListRemoteMediatorFactory
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import com.mustafakocer.feature_movies.shared.data.mapper.toDomainList
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieListRepositoryImpl @Inject constructor(
    private val mediatorFactory: MovieListRemoteMediatorFactory,
    private val movieListDao: MovieListDao,
    private val remoteKeyDao: RemoteKeyDao,
) : MovieListRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getMoviesByCategory(
        category: MovieCategory,
        language: String, // <-- YENİ PARAMETRE
    ): Flow<PagingData<MovieListItem>> {

        // Use simple pagination settings
        val paginationSettings = PaginationSettings.forCatalog

        return Pager(
            config = paginationSettings.toPagingConfig(),
            remoteMediator = mediatorFactory.create(category,language),
            pagingSourceFactory = {
                movieListDao.getMoviesForCategory(category.apiEndpoint,language)
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomainList() }
        }
    }

    override suspend fun refreshCategory(category: MovieCategory, language: String) { // <-- BURAYI DA GÜNCELLE
        movieListDao.deleteMoviesForCategory(category.apiEndpoint, language)
        remoteKeyDao.deleteRemoteKey(category.cacheKey, language)
    }

}