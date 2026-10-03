package com.mustafakocer.movieappfeaturebasedclean.feature.list.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.mustafakocer.core_database.pagination.PaginationSettings
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.list.data.local.dao.MovieListDao
import com.mustafakocer.movieappfeaturebasedclean.feature.list.data.mediator.MovieListRemoteMediatorFactory
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.mapper.toDomainList
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieCategory
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Paged movie lists per category: Room is the single source of truth and a RemoteMediator fills it
 * from the API. A new pager is created whenever the app language changes.
 */
@Singleton
class MovieListRepository @Inject constructor(
    private val mediatorFactory: MovieListRemoteMediatorFactory,
    private val movieListDao: MovieListDao,
    private val languageRepository: LanguageRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
    fun getMoviesByCategory(category: MovieCategory): Flow<PagingData<MovieListItem>> =
        languageRepository.languageFlow.flatMapLatest { languagePreference ->
            val language = languagePreference.apiParam
            Pager(
                config = PaginationSettings.forCatalog.toPagingConfig(),
                remoteMediator = mediatorFactory.create(category, language),
                pagingSourceFactory = {
                    movieListDao.getMoviesForCategory(category.apiEndpoint, language)
                }
            ).flow.map { pagingData ->
                pagingData.map { entity -> entity.toDomainList() }
            }
        }
}
