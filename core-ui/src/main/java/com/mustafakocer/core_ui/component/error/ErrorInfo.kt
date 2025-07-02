package com.mustafakocer.core_ui.component.error

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Error information data class
 */
data class ErrorInfo(
    val title: String,
    val description: String,
    val helpText: String = "",
    val icon: ImageVector,
    val emoji: String,
    val retryText: String = "Try Again",
)