package com.mustafakocer.feature_movies.details.presentation.contract

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.core_common.presentation.BaseUiState
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
    object DismissError : MovieDetailsEvent // hata mesajını kapatmak için

}

data class MovieDetailsUiState(
    val movie: MovieDetails? = null,
    val isSharing: Boolean = false,
    // BaseUiState'den gelen ortak durumlar
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState
