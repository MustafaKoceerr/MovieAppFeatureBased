package com.mustafakocer.feature_movies.search.domain.usecase

import androidx.paging.PagingData
import com.mustafakocer.feature_movies.search.domain.model.SearchQuery
import com.mustafakocer.feature_movies.search.domain.repository.SearchRepository
import com.mustafakocer.feature_movies.shared.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

/**
 * Search movies use case
 *
 * CLEAN ARCHITECTURE: Domain Layer - Business Logic
 * RESPONSIBILITY: Handle search business rules and validation
 */
class SearchMoviesUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {
    /**
     * Execute search with business logic validation
     */
    operator fun invoke(query: String): Flow<PagingData<Movie>> {
        val searchQuery = SearchQuery.create(query)

        // Business rule: Don't search if query is invalid
        if (!searchQuery.isValid) {
            return emptyFlow()
        }

        return searchRepository.searchMovies(searchQuery)
    }

}