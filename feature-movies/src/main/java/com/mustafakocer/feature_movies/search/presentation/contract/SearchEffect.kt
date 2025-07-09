package com.mustafakocer.feature_movies.search.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiEffect

/**
 * Search UI Effects - Side Effects
 *
 * RESPONSIBILITY: Define one-time UI side effects
 */
sealed interface SearchEffect : BaseUiEffect {

    /**
     * Navigate to movie details
     */
    data class NavigateToMovieDetail(
        val movieId: Int
    ) : SearchEffect

    /**
     * Navigate back to previous screen
     */
    object NavigateBack : SearchEffect

    /**
     * Show toast message
     */
    data class ShowSnackbar(val message: String) : SearchEffect
    /**
     * Hide keyboard
     */
    object HideKeyboard : SearchEffect
}