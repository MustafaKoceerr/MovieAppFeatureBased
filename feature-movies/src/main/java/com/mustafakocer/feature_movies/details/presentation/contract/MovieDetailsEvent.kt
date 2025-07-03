package com.mustafakocer.feature_movies.details.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiEvent

// ==================== EVENTS ====================

sealed interface MovieDetailsEvent : BaseUiEvent {
    /**
     * Load movie details by ID
     */
    data class LoadMovieDetails(val movieId: Int) : MovieDetailsEvent

    /**
     * Retry loading movie details
     */
    object RetryLoading : MovieDetailsEvent

    /**
     * Dismiss error
     */
    object DismissError : MovieDetailsEvent
}