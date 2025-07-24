package com.mustafakocer.feature_movies.search.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.feature_movies.search.domain.usecase.SearchMoviesUseCase
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEffect
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEvent
import com.mustafakocer.feature_movies.search.presentation.contract.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Manages the UI state and business logic for the movie search screen.
 *
 * This ViewModel orchestrates the search functionality by:
 * - Observing user input from the search field.
 * - Applying transformations like debouncing to the input stream for performance.
 * - Delegating the actual search operation to the [SearchMoviesUseCase].
 * - Caching the paginated results to handle configuration changes gracefully.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : BaseViewModel<SearchUiState, SearchEvent, SearchEffect>(
    SearchUiState()
) {
    // Architectural Decision: A private `MutableStateFlow` is used as the trigger for the search
    // pipeline. This decouples the raw user input from the actual search execution, allowing us
    // to apply operators like `debounce` and `distinctUntilChanged` before the search is performed.
    private val searchQueryFlow = MutableStateFlow("")

    init {
        // The reactive pipeline is set up once when the ViewModel is initialized.
        observeSearchQuery()
    }

    /**
     * Handles incoming user events from the UI.
     */
    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                // The UI state is updated instantly to reflect the user's typing in the text field.
                setState { copy(searchQuery = event.query) }
                // The internal flow is also updated, which will trigger the debounced search logic.
                searchQueryFlow.value = event.query
            }

            is SearchEvent.ClearSearch -> {
                setState { copy(searchQuery = "") }
                searchQueryFlow.value = ""
            }

            is SearchEvent.MovieClicked -> {
                sendEffect(SearchEffect.NavigateToMovieDetail(event.movieId))
            }

            is SearchEvent.BackClicked -> {
                sendEffect(SearchEffect.NavigateBack)
            }

            is SearchEvent.SearchSubmitted -> {
                // UX Rationale: Hiding the keyboard allows the user to see the full list of results.
                sendEffect(SearchEffect.HideKeyboard)
            }
        }
    }

    /**
     * Sets up the reactive pipeline that listens to the search query, performs the search,
     * and updates the UI state with the results.
     */
    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        val searchResultsFlow = searchMoviesUseCase(
            queryFlow = searchQueryFlow
                // Performance/UX Rationale: `debounce` waits for the user to stop typing for a
                // specified duration (500ms) before initiating a search. This prevents excessive
                // API calls and provides a smoother user experience.
                .debounce(500L)
                // Performance Rationale: `distinctUntilChanged` ensures that the search is not
                // re-triggered if the query has not actually changed.
                .distinctUntilChanged()
        )

            .cachedIn(viewModelScope)

        setState { copy(searchResults = searchResultsFlow) }
    }
}