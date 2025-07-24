package com.mustafakocer.feature_movies.list.presentation.contract

import androidx.paging.PagingData
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.presentation.BaseUiEffect
import com.mustafakocer.core_domain.presentation.BaseUiEvent
import com.mustafakocer.core_domain.presentation.BaseUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// --- STATE ---

/**
 * Represents the complete UI state for the movie list screen.
 *
 * @property movies The core data stream for the UI. It's a Flow of PagingData which the UI
 *                  (e.g., a LazyColumn) will collect to display a paginated list of movies.
 * @property category The specific movie category being displayed, used to show a relevant title.
 * @property isLoading A general loading flag, potentially for actions outside of Paging's scope.
 * @property isRefreshing A flag for user-initiated refresh actions (e.g., pull-to-refresh).
 * @property error Holds any critical error that prevents the screen from functioning.
 */
data class MovieListUiState(
    // Architectural Decision: The `Flow<PagingData>` is held directly in the UI state.
    // This is the standard pattern for Jetpack Paging 3. The ViewModel exposes this flow, and the
    // UI collects it using the `collectAsLazyPagingItems()` extension function, which automatically
    // handles the subscription, data updates, and loading states for the list items.
    val movies: Flow<PagingData<MovieListItem>> = emptyFlow(),
    val category: MovieCategory? = null,

    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState

// --- EVENT ---

sealed interface MovieListEvent : BaseUiEvent {

    data class MovieClicked(val movieId: Int) : MovieListEvent

    object BackClicked : MovieListEvent
}

// --- EFFECT ---


sealed interface MovieListEffect : BaseUiEffect {

    data class NavigateToMovieDetail(val movieId: Int) : MovieListEffect

    object NavigateBack : MovieListEffect

    data class ShowSnackbar(val message: String) : MovieListEffect
}