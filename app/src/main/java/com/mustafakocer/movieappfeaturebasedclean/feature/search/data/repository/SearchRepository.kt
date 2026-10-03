package com.mustafakocer.movieappfeaturebasedclean.feature.search.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.mustafakocer.core_database.pagination.PaginationSettings
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.api.MovieApiService
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.feature.search.data.paging.SearchPagingSource
import com.mustafakocer.movieappfeaturebasedclean.feature.search.domain.model.SearchQuery
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Paged movie search. Searches again when the query or the app language changes; queries that
 * are not valid (too short/long) produce an empty result instead of hitting the API.
 */
@Singleton
class SearchRepository @Inject constructor(
    private val movieApiService: MovieApiService,
    private val languageRepository: LanguageRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun searchMovies(queryFlow: Flow<String>): Flow<PagingData<MovieListItem>> =
        // The language flow only acts as a re-trigger; the query is what we search for.
        combine(queryFlow, languageRepository.languageFlow) { query, _ -> SearchQuery(query) }
            .flatMapLatest { searchQuery ->
                if (!searchQuery.isValid) {
                    flowOf(PagingData.empty())
                } else {
                    Pager(
                        config = PaginationSettings.forSearch.toPagingConfig(),
                        pagingSourceFactory = {
                            SearchPagingSource(
                                movieApiService = movieApiService,
                                searchQuery = searchQuery.cleanQuery
                            )
                        }
                    ).flow
                }
            }
}
