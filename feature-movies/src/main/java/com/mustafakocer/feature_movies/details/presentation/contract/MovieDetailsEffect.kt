package com.mustafakocer.feature_movies.details.presentation.contract

// ==================== EFFECTS ====================

/**
 * TEACHING MOMENT: Event vs Effect - The Ultimate Guide
 *
 * GOLDEN RULE:
 * - Events = "What the user DID" (Input)
 * - Effects = "What should happen in the outside world" (Output)
 */

// ==================== EFFECTS (Side Effects) ====================

sealed class MovieDetailsEffect {
    /**
     * ✅ EFFECT: Actually perform navigation
     */
    object NavigateBack : MovieDetailsEffect()

    /**
     * ✅ EFFECT: Show system share dialog
     */
    data class ShareContent(
        val title: String,
        val content: String
    ) : MovieDetailsEffect()

    /**
     * ✅ UNIFIED: Single snackbar effect for all messages
    * @param message The message to display
    * @param isError Whether this is an error message (affects styling)
    * @param isPersistent Whether to show indefinitely (for network status)
    */
    data class ShowSnackbar(
        val message: String,
        val isError: Boolean = false,
    ) : MovieDetailsEffect()

}