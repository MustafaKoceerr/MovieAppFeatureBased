package com.mustafakocer.feature_movies.details.presentation.contract

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_ui.component.error.ErrorInfo
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails

/**
 * Movie details UI state using sealed interface approach
 *
 * NETFLIX APPROACH:
 * ✅ Clear state separation
 * ✅ No impossible states
 * ✅ Type-safe state handling
 */

sealed interface MovieDetailsUiState {

    object Loading : MovieDetailsUiState

    data class Error(
        val exception: AppException,
        val errorInfo: ErrorInfo,
    ) : MovieDetailsUiState

    data class Success(
        val movieDetails: MovieDetails,
        val isSharing: Boolean = false,
    ) : MovieDetailsUiState {
        /**
         * Check if sharing is in progress
         */
        fun isSharingProgress(): Boolean = isSharing
    }


}