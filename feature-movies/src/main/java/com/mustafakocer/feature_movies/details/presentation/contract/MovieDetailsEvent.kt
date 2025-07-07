package com.mustafakocer.feature_movies.details.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiEvent

// ==================== EVENTS ====================

sealed interface MovieDetailsEvent : BaseUiEvent {

    /**
     * Retry loading movie details
     */
    object RetryLoading : MovieDetailsEvent

    /**
     * Dismiss error
     */
    object DismissError : MovieDetailsEvent
}