package com.mustafakocer.core_common.presentation

/**
 * TEACHING MOMENT: Common UI Effects
 *
 * Separate file because this will grow with more common effects
 * and might have complex implementations later
 */

sealed interface CommonUiEffect : BaseUiEffect {

    // ==================== MESSAGE EFFECTS ====================
    /**
     * Show toast message
     *
     * Usage:
     * _uiEffect.emit(CommonUiEffect.ShowToast("Movie saved!"))
     */
    data class ShowToast(
        val message: String,
        val duration: ToastDuration = ToastDuration.SHORT
    ) : CommonUiEffect

    /**
     * Show snackbar with optional action
     *
     * Usage:
     * _uiEffect.emit(
     *     CommonUiEffect.ShowSnackbar(
     *         message = "Movie deleted",
     *         actionLabel = "Undo"
     *     )
     * )
     */
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val duration: SnackbarDuration = SnackbarDuration.SHORT
    ) : CommonUiEffect

    // ==================== DIALOG EFFECTS ====================
    /**
     * Show alert dialog
     */
    data class ShowDialog(
        val title: String,
        val message: String,
        val positiveButton: String = "OK",
        val negativeButton: String? = null
    ) : CommonUiEffect

    /**
     * Dismiss current dialog
     */
    object DismissDialog : CommonUiEffect

    // ==================== SYSTEM EFFECTS ====================

    /**
     * Hide soft keyboard
     */
    object HideKeyboard : CommonUiEffect

}

/**
 * Toast duration options
 */
enum class ToastDuration { SHORT, LONG }

/**
 * Snackbar duration options
 */
enum class SnackbarDuration { SHORT, LONG, INDEFINITE }