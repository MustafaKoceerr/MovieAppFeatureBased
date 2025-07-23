package com.mustafakocer.core_ui.component.error

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A data class that encapsulates all necessary information to render a standardized error component.
 *
 * Architectural Note:
 * This class creates a consistent data contract for any UI component that needs to display an
 * error state. By passing this single object, we ensure that all error screens across the
 * application are standardized, promoting a consistent user experience and simplifying the
 * implementation of error-displaying Composables.
 *
 * @property title The main headline for the error message.
 * @property description A more detailed explanation of what went wrong.
 * @property helpText Optional text offering a hint or context to the user.
 * @property icon A vector graphic representing the type of error.
 * @property emoji A simple emoji to add a bit of character to the error display.
 * @property retryText The text for the action button, typically for retrying the failed operation.
 */
data class ErrorInfo(
    val title: String,
    val description: String,
    val helpText: String = "",
    val icon: ImageVector,
    val emoji: String,
    val retryText: String = "Try Again",
)