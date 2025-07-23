package com.mustafakocer.feature_movies.search.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.mustafakocer.core_database.pagination.PaginationSettings
import com.mustafakocer.feature_movies.search.data.paging.SearchPagingSource
import com.mustafakocer.feature_movies.search.domain.model.SearchQuery
import com.mustafakocer.feature_movies.search.domain.repository.SearchRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implements the [SearchRepository] interface, providing a concrete data-handling strategy for movie searches.
 *
 * Architectural Decision: This repository implements a network-only pagination strategy. Unlike other
 * features that use a `RemoteMediator` and a local database cache, the search functionality fetches
 * data directly from the API on demand. This is a pragmatic application of the KISS (Keep It Simple, Stupid)
 * principle for the following reasons:
 * -   **Volatility:** Search results are highly dynamic and depend on a transient user query. Caching them
 *     provides little value and adds significant complexity.
 * -   **Simplicity:** Avoiding a database cache and `RemoteMediator` leads to a much simpler, more
 *     maintainable implementation for this specific use case.
 * -   **Performance:** For a search feature, users expect fresh, real-time results, making direct
 *     network access the most appropriate approach.
 */
@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService,
) : SearchRepository {

    /**
     * Searches for movies using a network-only PagingSource.
     *
     * @param query The [SearchQuery] object containing the validated search term.
     * @return A [Flow] of [PagingData] containing the search results.
     */
    override fun searchMovies(query: SearchQuery): Flow<PagingData<MovieListItem>> {
        return Pager(
            config = PaginationSettings.forSearch.toPagingConfig(),
            pagingSourceFactory = {
                SearchPagingSource(
                    movieApiService = movieApiService,
                    // We pass the sanitized `cleanQuery` from the domain model to the PagingSource,
                    // ensuring that the API is called with a properly formatted search term.
                    searchQuery = query.cleanQuery
                )
            }
        ).flow
    }
}