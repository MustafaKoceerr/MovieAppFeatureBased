package com.mustafakocer.feature_movies.home.presentation.contract

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.presentation.BaseUiEffect
import com.mustafakocer.core_domain.presentation.BaseUiEvent
import com.mustafakocer.core_domain.presentation.BaseUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem

/**
 * Defines the UI state for the Home screen.
 *
 * @param categories A map where each key is a `MovieCategory` and the value is the list of movies for that category.
 * @param isLoading A flag indicating if the initial data load is in progress.
 * @param isRefreshing A flag indicating if a refresh operation is in progress.
 * @param error The [AppException] to be displayed, if any.
 */
data class HomeUiState(
    val categories: Map<MovieCategory, List<MovieListItem>> = emptyMap(),
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState {
    val showFullScreenLoading: Boolean
        get() = isLoading && categories.isEmpty()

    val showFullScreenError: Boolean
        get() = error != null && categories.isEmpty()
}


sealed interface HomeEvent : BaseUiEvent {
    object Refresh : HomeEvent

    data class MovieClicked(val movieId: Int) : HomeEvent

    data class ViewAllClicked(val category: MovieCategory) : HomeEvent

    object SettingsClicked : HomeEvent

    object SearchClicked : HomeEvent

    object AccountClicked : HomeEvent
}

sealed interface HomeEffect : BaseUiEffect {
    data class NavigateToMovieDetails(val movieId: Int) : HomeEffect

    data class NavigateToMovieList(val categoryEndpoint: String) : HomeEffect

    object NavigateToSettings : HomeEffect

    object NavigateToSearch : HomeEffect

    object NavigateToAccount : HomeEffect

    data class ShowSnackbar(val message: String) : HomeEffect
}