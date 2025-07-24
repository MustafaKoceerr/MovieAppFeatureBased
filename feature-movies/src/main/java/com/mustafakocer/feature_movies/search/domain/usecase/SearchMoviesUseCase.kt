package com.mustafakocer.feature_movies.search.domain.usecase

import androidx.paging.PagingData
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.search.domain.model.SearchQuery
import com.mustafakocer.feature_movies.search.domain.repository.SearchRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * A use case that orchestrates the movie search functionality by reacting to both query
 * and language changes.
 *
 * Architectural Decision: This use case centralizes the core business logic for searching.
 * It abstracts the complexity of combining multiple data streams (query and language) and
 * enforcing validation rules, providing a simple, single entry point for the ViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchMoviesUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val languageRepository: LanguageRepository,
) {
    /**
     * Combines a stream of search queries with the application's language stream to produce
     * a paginated flow of search results.
     *
     * @param queryFlow A [Flow] of search query strings from the ViewModel.
     * @return A [Flow] of [PagingData] that is reactive to both query and language changes.
     */
    operator fun invoke(queryFlow: Flow<String>): Flow<PagingData<MovieListItem>> {
        // Architectural Decision: `combine` is used to merge the user's search query with the
        // current language from the LanguageRepository. This makes the entire search pipeline
        // reactive. If the user changes the app's language while on the search screen, this
        // flow will re-emit, triggering a new search with the updated language without any
        // extra logic in the ViewModel.
        val combinedFlow = combine(queryFlow, languageRepository.languageFlow) { query, _ ->
            query // We only need the query for the next step; the language flow acts as a trigger.
        }

        // Architectural Decision: `flatMapLatest` is critical for efficiency and correctness.
        // When the combined flow emits a new query (or the language changes), `flatMapLatest`
        // automatically cancels the previous search operation and starts a new one. This prevents
        // race conditions and avoids executing searches for outdated queries.
        return combinedFlow.flatMapLatest { query ->
            // The raw string is converted into a `SearchQuery` value object. This encapsulates
            // validation logic within the domain layer, keeping the use case clean.
            val searchQuery = SearchQuery.create(query)
            if (!searchQuery.isValid) {
                flowOf(PagingData.empty())
            } else {
                // If the query is valid, delegate the actual data fetching to the repository.
                searchRepository.searchMovies(searchQuery)
            }
        }
    }
}