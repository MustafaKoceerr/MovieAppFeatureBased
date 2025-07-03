package com.mustafakocer.feature_movies.details.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.core_common.result.onError
import com.mustafakocer.core_common.result.onSuccess
import com.mustafakocer.core_ui.component.error.toErrorInfoOrFallback
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails
import com.mustafakocer.feature_movies.details.domain.usecase.GetMovieDetailsUseCase
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Movie details ViewModel with share functionality
 *
 * CLEAN ARCHITECTURE: Presentation layer
 * RESPONSIBILITY: Handle movie details UI logic and share functionality
 */

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
) : ViewModel(), UiContract<MovieDetailsUiState, MovieDetailsEvent, MovieDetailsEffect> {
    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    override val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MovieDetailsEffect>()
    override val uiEffect: SharedFlow<MovieDetailsEffect> = _uiEffect.asSharedFlow()

    override fun onEvent(event: MovieDetailsEvent) {
        when (event) {
            is MovieDetailsEvent.LoadMovieDetails -> loadMovieDetails(event.movieId)
            is MovieDetailsEvent.RetryLoading -> retryLoading()
            is MovieDetailsEvent.DismissError -> dismissError()
        }
    }

    // ==================== PUBLIC METHODS ====================

    /**
     * Share movie details (called directly from UI)
     */
    fun shareMovie() {
        val currentState = _uiState.value
        if (currentState is MovieDetailsUiState.Success) {
            viewModelScope.launch {
                // set sharing state
                _uiState.value = currentState.copy(isSharing = true)

                try {
                    val movieDetails = currentState.movieDetails
                    val shareTitle = "Check out this movie!"
                    val shareContent = buildShareContent(movieDetails)

                    _uiEffect.emit(
                        MovieDetailsEffect.ShareContent(
                            title = shareTitle,
                            content = shareContent
                        )
                    )
                    showToast("Sharing ${movieDetails.title}")
                } catch (e: Exception) {
                    showToast("Failed to share movie")
                } finally {
                    // Reset sharing state
                    _uiState.value = currentState.copy(isSharing = false)
                }
            }
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _uiEffect.emit(MovieDetailsEffect.NavigateBack)
        }
    }

    // ==================== PRIVATE METHODS ====================

    private fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading

            getMovieDetailsUseCase(movieId).collect { uiState ->
                uiState.onSuccess { movieDetails ->
                    _uiState.value = MovieDetailsUiState.Success(movieDetails)
                }.onError { exception ->
                    val errorInfo = exception.toErrorInfoOrFallback()
                    _uiState.value = MovieDetailsUiState.Error(exception, errorInfo)
                }
            }
        }
    }

    private fun retryLoading() {
        val currentState = _uiState.value
        if (currentState is MovieDetailsUiState.Error) {
            // We need to store the movie ID to retry
            // For now, show a message that retry needs the movie ID
            showToast("Please go back and try again")
        }
    }

    private fun dismissError() {
        val currentState = _uiState.value
        if (currentState is MovieDetailsUiState.Error) {
            // Option 1: Clear error and return to loading state (if you have movieId stored)
            // _uiState.value = MovieDetailsUiState.Loading
            // loadMovieDetails(storedMovieId)

            // Option 2: Clear error and navigate back (current approach)
            navigateBack()

            // Option 3: Clear error and show empty state
            // _uiState.value = MovieDetailsUiState.Idle (if you have this state)
        }
    }

    // ==================== HELPER METHODS ====================

    private fun buildShareContent(movieDetails: MovieDetails): String {
        return buildString {
            append("🎬 ${movieDetails.title}\n\n")

            if (movieDetails.hasTagline) {
                append("\"${movieDetails.tagline}\"\n\n")
            }

            append("⭐ Rating: ${String.format("%.1f", movieDetails.voteAverage)}/10\n")
            append("📅 Release: ${movieDetails.releaseDate}\n")

            if (movieDetails.runtime != null) {
                val hours = movieDetails.runtime / 60
                val minutes = movieDetails.runtime % 60
                val runtimeText = when {
                    hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
                    hours > 0 -> "${hours}h"
                    else -> "${minutes}m"
                }
                append("⏱️ Runtime: $runtimeText\n")
            }

            if (movieDetails.genres.isNotEmpty()) {
                append("🎭 Genres: ${movieDetails.genres.joinToString { it.name }}\n")
            }

            append("\n📖 ${movieDetails.overview}")

            append("\n\n#Movies #Cinema #${movieDetails.title.replace(" ", "")}")
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.emit(MovieDetailsEffect.ShowToast(message))
        }
    }

    private fun showSnackbar(message: String, actionLabel: String? = null) {
        viewModelScope.launch {
            _uiEffect.emit(MovieDetailsEffect.ShowSnackbar(message, actionLabel))
        }
    }
}