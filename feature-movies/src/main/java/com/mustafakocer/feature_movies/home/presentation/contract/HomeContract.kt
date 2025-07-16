package com.mustafakocer.feature_movies.home.presentation.contract

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem

// ==================== STATE ====================
data class HomeUiState(
    // Döngü kurmak ve ölçeklenebilirlik için en iyi yapı.
    val categories: Map<MovieCategory, List<MovieListItem>> = emptyMap(),

    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState {
    // Computed properties
    val showFullScreenLoading: Boolean
        get() = isLoading && categories.isEmpty()

    val showFullScreenError: Boolean
        get() = error != null && categories.isEmpty()
}

// ==================== EVENT ====================
sealed interface HomeEvent : BaseUiEvent {
    object Refresh : HomeEvent
    data class MovieClicked(val movieId: Int) : HomeEvent
    data class ViewAllClicked(val category: MovieCategory) : HomeEvent
    object ProfileClicked : HomeEvent
    object SearchClicked : HomeEvent
}

// ==================== EFFECT ====================
sealed interface HomeEffect : BaseUiEffect {
    data class NavigateToMovieDetails(val movieId: Int) : HomeEffect
    data class NavigateToMovieList(val categoryEndpoint: String) : HomeEffect
    object NavigateToSettings : HomeEffect
    object NavigateToSearch : HomeEffect
    data class ShowSnackbar(val message: String) : HomeEffect
}