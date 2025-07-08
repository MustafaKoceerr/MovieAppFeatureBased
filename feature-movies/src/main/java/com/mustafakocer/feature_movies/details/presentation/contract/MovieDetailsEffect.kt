package com.mustafakocer.feature_movies.details.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.feature_movies.home.domain.model.HomeContent

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
     * - User action: No (system action)
     * - State change: No (external to this screen)
     * - One-time: Yes
     * - External system: Yes (Navigation)
     *
     * TRIGGERED BY: BackPressed event, DismissError event
     */
    object NavigateBack : MovieDetailsEffect()

    /**
     * ✅ EFFECT: Show system share dialog
     * - User action: No (system action)
     * - State change: No (external dialog)
     * - One-time: Yes
     * - External system: Yes (Android Share)
     *
     * TRIGGERED BY: ShareMovie event (after preparing content)
     */
    data class ShareContent(
        val title: String,
        val content: String
    ) : MovieDetailsEffect()

    /**
     * ✅ EFFECT: Show snackbar notification
     * - User action: No (system action)
     * - State change: No (temporary UI element)
     * - One-time: Yes
     * - External system: Yes (Snackbar system)
     */
    data class ShowSnackbar(
        val message: String,
        val isError: Boolean = false
    ) : MovieDetailsEffect()

    /**
     * ✅ EFFECT: Show network snackbar
     * - User action: No (automatic notification)
     * - State change: No (temporary UI element)
     * - One-time: Yes
     * - External system: Yes (Snackbar system)
     *
     * NOTE: DismissNetworkSnackbar is EVENT because user dismisses it!
     */
    data class ShowNetworkSnackbar(
        val message: String,
        val isOffline: Boolean = true
    ) : MovieDetailsEffect()
}
