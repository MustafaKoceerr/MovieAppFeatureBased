package com.mustafakocer.feature_movies.details.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.core_common.result.NetworkAwareUiState
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
 * TEACHING MOMENT: Network-Aware ViewModel with Corrected Event/Effect Pattern
 *
 * CLEAN ARCHITECTURE PRINCIPLES:
 * ✅ Presentation Layer - Handles UI logic and state management
 * ✅ Dependency Inversion - Depends on use case abstraction
 * ✅ Single Responsibility - Manages only movie details presentation state
 * ✅ Open/Closed - Open for extension (new events), closed for modification
 *
 * MVI PATTERN IMPLEMENTATION:
 * ✅ Unidirectional data flow: Event → State → Effect
 * ✅ Immutable state management
 * ✅ Side effects handled separately from state
 * ✅ Event-driven architecture
 *
 * NETWORK-AWARE FEATURES:
 * ✅ Preserves data during connectivity issues
 * ✅ Shows appropriate UI based on data availability
 * ✅ Manages network error snackbars
 * ✅ Handles connectivity state transitions
 */

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), UiContract<MovieDetailsUiState, MovieDetailsEvent, MovieDetailsEffect> {

    // MVI STATE MANAGEMENT
    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.InitialLoading)
    override val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MovieDetailsEffect>()
    override val uiEffect: SharedFlow<MovieDetailsEffect> = _uiEffect.asSharedFlow()

    // PROCESS DEATH RESILIENCE
    private val movieId: Int = savedStateHandle.get<Int>("movieId")
        ?: throw IllegalStateException("movieId is required in navigation arguments")

    init {
        // Auto-load details when ViewModel is created
        loadMovieDetails()
    }

    // ==================== EVENT HANDLING ====================

    override fun onEvent(event: MovieDetailsEvent) {
        when (event) {
            is MovieDetailsEvent.RetryLoading -> handleRetryLoading()
            is MovieDetailsEvent.ShareMovie -> handleShareMovie()
            is MovieDetailsEvent.DismissError -> handleDismissError()
            is MovieDetailsEvent.DismissNetworkSnackbar -> handleDismissNetworkSnackbar()
            is MovieDetailsEvent.BackPressed -> handleBackPressed()
        }
    }

    // ==================== PRIVATE EVENT HANDLERS ====================
    /**
     * Handle retry loading - user tapped retry button
     */
    private fun handleRetryLoading() {
        loadMovieDetails()
    }

    /**
     * Handle share movie - user tapped share button
     */
    private fun handleShareMovie() {
        val currentMovieDetails = _uiState.value.movieDetailsOrNull
        if (currentMovieDetails == null) {
            viewModelScope.launch {
                _uiEffect.emit(
                    MovieDetailsEffect.ShowSnackbar(
                        message = "Movie details not available for sharing",
                        isError = true
                    )
                )
            }
            return
        }

        viewModelScope.launch {

            // Set sharing state
            updateSharingState(true)

            try {
                val shareTitle = "Check out this movie!"
                val shareContent = buildShareContent(currentMovieDetails)

                // Emit share effect
                _uiEffect.emit(
                    MovieDetailsEffect.ShareContent(
                        title = shareTitle,
                        content = shareContent
                    )
                )

            } catch (e: Exception) {
                _uiEffect.emit(
                    MovieDetailsEffect.ShowSnackbar(
                        message = "Failed to share movie",
                        isError = true
                    )
                )
            } finally {
                // Reset sharing state
                updateSharingState(false)
            }
        }
    }

    /**
     * Handle dismiss error - user dismissed error dialog
     */
    private fun handleDismissError() {
        // Navigate back when error is dismissed
        viewModelScope.launch {
            _uiEffect.emit(MovieDetailsEffect.NavigateBack)
        }
    }


    /**
     * Handle dismiss network snackbar - user tapped X on network snackbar
     */
    private fun handleDismissNetworkSnackbar() {
        val currentState = _uiState.value
        if (currentState is MovieDetailsUiState.SuccessWithNetworkError) {
            _uiState.value = currentState.copy(showNetworkSnackbar = false)
        }
    }

    /**
     * Handle back pressed - user pressed back button or navigation back
     */
    private fun handleBackPressed() {
        // Could add business logic here (e.g., unsaved changes check)
        viewModelScope.launch {
            _uiEffect.emit(MovieDetailsEffect.NavigateBack)
        }
    }

    // ==================== PRIVATE BUSINESS LOGIC ====================

    /**
     * Load movie details using network-aware flow
     *
     * BEHAVIOR:
     * - Maps NetworkAwareUiState to MovieDetailsUiState
     * - Handles network snackbar emissions
     * - Preserves data during connectivity issues
     */
    private fun loadMovieDetails() {
        viewModelScope.launch {
            getMovieDetailsUseCase(movieId).collect { networkAwareState ->
                when (networkAwareState) {
                    is NetworkAwareUiState.Idle -> {
                        // Should not happen in this flow, but handle gracefully
                        _uiState.value = MovieDetailsUiState.InitialLoading
                    }

                    is NetworkAwareUiState.InitialLoading -> {
                        _uiState.value = MovieDetailsUiState.InitialLoading
                    }

                    is NetworkAwareUiState.RefreshLoading -> {
                        _uiState.value = MovieDetailsUiState.RefreshLoading(
                            movieDetails = networkAwareState.currentData,
                            isSharing = _uiState.value.isSharingInProgress
                        )
                    }

                    is NetworkAwareUiState.Success -> {
                        _uiState.value = MovieDetailsUiState.Success(
                            movieDetails = networkAwareState.data,
                            isSharing = _uiState.value.isSharingInProgress,
                        )
                    }

                    is NetworkAwareUiState.SuccessWithNetworkError -> {
                        _uiState.value = MovieDetailsUiState.SuccessWithNetworkError(
                            movieDetails = networkAwareState.data,
                            networkError = networkAwareState.networkError,
                            isSharing = _uiState.value.isSharingInProgress,
                            showNetworkSnackbar = networkAwareState.showSnackbar
                        )

                        // Emit network snackbar effect if needed
                        if (networkAwareState.showSnackbar) {
                            _uiEffect.emit(
                                MovieDetailsEffect.ShowNetworkSnackbar(
                                    message = networkAwareState.networkError.userMessage,
                                    isOffline = true
                                )
                            )
                        }
                    }

                    is NetworkAwareUiState.Error -> {
                        val errorInfo = networkAwareState.exception.toErrorInfoOrFallback()
                        _uiState.value = MovieDetailsUiState.Error(
                            exception = networkAwareState.exception,
                            errorInfo = errorInfo
                        )
                    }
                }
            }
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Update sharing state while preserving other state properties
     */
    private fun updateSharingState(isSharing: Boolean) {
        _uiState.value = when (val currentState = _uiState.value) {
            is MovieDetailsUiState.Success -> currentState.copy(isSharing = isSharing)
            is MovieDetailsUiState.SuccessWithNetworkError -> currentState.copy(isSharing = isSharing)
            is MovieDetailsUiState.RefreshLoading -> currentState.copy(isSharing = isSharing)
            else -> currentState // Don't update sharing state for loading/error states
        }
    }

    /**
     * Build formatted content for sharing
     */
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

}
