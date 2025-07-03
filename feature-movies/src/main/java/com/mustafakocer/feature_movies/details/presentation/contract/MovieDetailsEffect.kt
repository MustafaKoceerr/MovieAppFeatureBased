package com.mustafakocer.feature_movies.details.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.feature_movies.home.domain.model.HomeContent

// ==================== EFFECTS ====================

sealed interface MovieDetailsEffect : BaseUiEffect {

    /**
     * Navigate back to previous screen
     */
    object NavigateBack : MovieDetailsEffect

    /**
     * Show sharing dialog/intent
     */
    data class ShareContent(
        val title: String,
        val content: String,
    ) : MovieDetailsEffect

    /**
     * Show toast message
     */
    data class ShowToast(val message: String) : MovieDetailsEffect

    /**
     * Show snackbar message
     */
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
    ) : MovieDetailsEffect
}