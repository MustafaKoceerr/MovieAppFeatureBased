package com.mustafakocer.feature_movies.search.presentation.contract

import androidx.paging.PagingData
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.presentation.BaseUiEffect
import com.mustafakocer.core_domain.presentation.BaseUiEvent
import com.mustafakocer.core_domain.presentation.BaseUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// --- STATE ---

/**
 * Represents the complete UI state for the movie search screen.
 *
 * @property searchQuery The current text entered by the user in the search input field.
 * @property searchResults The reactive stream of paginated search results from the Paging 3 library.
 * @property isLoading A flag to indicate when a search is actively being performed (e.g., after the debounce period).
 * @property isRefreshing A flag for user-initiated refresh actions. Kept for consistency, though Paging has its own refresh state.
 * @property error Holds any critical error that prevents the screen from functioning.
 */
data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: Flow<PagingData<MovieListItem>> = emptyFlow(),
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState {

    val canSearch: Boolean
        get() = searchQuery.trim().length >= 3

    val showInitialPrompt: Boolean
        get() = searchQuery.isEmpty()
}

// --- EVENT ---

sealed interface SearchEvent : BaseUiEvent {

    data class QueryChanged(val query: String) : SearchEvent

    object ClearSearch : SearchEvent

    data class MovieClicked(val movieId: Int) : SearchEvent

    object BackClicked : SearchEvent

    object SearchSubmitted : SearchEvent
}

// --- EFFECT ---

sealed interface SearchEffect : BaseUiEffect {

    data class NavigateToMovieDetail(val movieId: Int) : SearchEffect

    object NavigateBack : SearchEffect

    data class ShowSnackbar(val message: String) : SearchEffect

    object HideKeyboard : SearchEffect
}