package com.mustafakocer.feature_movies.details.presentation.contract

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.presentation.BaseUiEffect
import com.mustafakocer.core_domain.presentation.BaseUiEvent
import com.mustafakocer.core_domain.presentation.BaseUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails


sealed interface MovieDetailsEffect : BaseUiEffect {
    object NavigateBack : MovieDetailsEffect

    data class ShareContent(val content: String) : MovieDetailsEffect

    data class ShowSnackbar(val message: String, val isError: Boolean = false) : MovieDetailsEffect
}


sealed interface MovieDetailsEvent : BaseUiEvent {
    object Refresh : MovieDetailsEvent

    data class ShareMovie(val content: String) : MovieDetailsEvent

    object BackPressed : MovieDetailsEvent

    object DismissError : MovieDetailsEvent
}

/**
 * Defines the UI state for the Movie Details screen.
 *
 * @param movie The detailed movie information to be displayed.
 * @param isSharing A flag to indicate if a share operation is in progress (currently unused).
 * @param isLoading A flag indicating if the initial data load is in progress.
 * @param isRefreshing A flag indicating if a refresh operation is in progress.
 * @param error The [AppException] to be displayed, if any.
 */
data class MovieDetailsUiState(
    val movie: MovieDetails? = null,
    val isSharing: Boolean = false,
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState