package com.mustafakocer.feature_movies.list.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiEvent

// ==================== UI EVENTS ====================

sealed interface MovieListEvent: BaseUiEvent{
    data class InitializeScreen(
        val categoryEndpoint: String,
        val categoryTitle: String
    ): MovieListEvent

    object RefreshMovies : MovieListEvent
    object RetryClicked : MovieListEvent
    data class MovieClicked(
        val movieId: Int,
        val movieTitle: String
    ) : MovieListEvent
}