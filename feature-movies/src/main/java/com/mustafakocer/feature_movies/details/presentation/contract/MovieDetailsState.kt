package com.mustafakocer.feature_movies.details.presentation.contract

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_ui.component.error.ErrorInfo
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails

sealed class MovieDetailsUiState {

    /**
     * Initial loading state when fetching movie details for the first time
     */
    object InitialLoading : MovieDetailsUiState()

    /**
     * Success state with movie details loaded
     * @param movieDetails The loaded movie details
     * @param isSharing Whether sharing is currently in progress
     * @param isOffline Whether currently offline (for UI indicators)
     */
    data class Success(
        val movieDetails: MovieDetails,
        val isSharing: Boolean = false,
        val isOffline: Boolean = false
    ) : MovieDetailsUiState()

    /**
     * Success state with data preserved but network issues
     * Shows data but indicates connectivity problems via snackbar
     * @param movieDetails The preserved movie details
     * @param networkError The network-related error
     * @param isSharing Whether sharing is currently in progress
     * @param showNetworkSnackbar Whether to show network error snackbar
     */
    data class SuccessWithNetworkError(
        val movieDetails: MovieDetails,
        val networkError: AppException,
        val isSharing: Boolean = false,
        val showNetworkSnackbar: Boolean = true
    ) : MovieDetailsUiState()

    /**
     * Refreshing state while updating existing data
     * Shows current data while loading updated data in background
     * @param movieDetails Current movie details being displayed
     * @param isSharing Whether sharing is currently in progress
     */
    data class RefreshLoading(
        val movieDetails: MovieDetails,
        val isSharing: Boolean = false
    ) : MovieDetailsUiState()

    /**
     * Error state when no data exists and loading failed
     * @param exception The error that occurred
     * @param errorInfo UI-friendly error information
     */
    data class Error(
        val exception: AppException,
        val errorInfo: ErrorInfo
    ) : MovieDetailsUiState()

    // UTILITY PROPERTIES FOR UI LOGIC

    /**
     * Check if currently loading (initial or refresh)
     */
    val isLoading: Boolean
        get() = this is InitialLoading || this is RefreshLoading

    /**
     * Check if movie details are available
     */
    val hasMovieDetails: Boolean
        get() = this is Success || this is SuccessWithNetworkError || this is RefreshLoading

    /**
     * Get movie details if available
     */
    val movieDetailsOrNull: MovieDetails?
        get() = when (this) {
            is Success -> movieDetails
            is SuccessWithNetworkError -> movieDetails
            is RefreshLoading -> movieDetails
            else -> null
        }

    /**
     * Check if should show network snackbar
     */
    val shouldShowNetworkSnackbar: Boolean
        get() = this is SuccessWithNetworkError && showNetworkSnackbar

    /**
     * Check if sharing is in progress
     */
    val isSharingInProgress: Boolean
        get() = when (this) {
            is Success -> isSharing
            is SuccessWithNetworkError -> isSharing
            is RefreshLoading -> isSharing
            else -> false
        }
}
