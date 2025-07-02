package com.mustafakocer.core_ui.component.error

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WifiOff

/**
 * UPDATED: Generic error message factory with hybrid approach
 *
 * NEW: createFrom() method for global infrastructure errors
 * EXISTING: Specific error methods for contextual usage
 */
object GenericErrorMessageFactory {

    // ✅ NEW: Global error mapping (Infrastructure errors only)
    fun createFrom(type: GeneralErrorType, message: String? = null): ErrorInfo {
        return when (type) {
            GeneralErrorType.NO_INTERNET -> noInternetConnection()
            GeneralErrorType.SERVER_TIMEOUT -> serverTimeout()
            GeneralErrorType.SERVER_UNREACHABLE -> serverError()
            GeneralErrorType.SERVER_ERROR -> serverError()
            GeneralErrorType.RATE_LIMITED -> rateLimitExceeded()
            GeneralErrorType.UNKNOWN -> unknownError(message)
        }
    }

    // ✅ EXISTING: Specific error methods (kept for contextual usage)

    fun noInternetConnection() = ErrorInfo(
        title = "No Internet Connection",
        description = "We need an internet connection to discover amazing movies for you. Please check your connection and try again.",
        helpText = "💡 Make sure WiFi or mobile data is enabled",
        icon = Icons.Default.WifiOff,
        emoji = "📡",
        retryText = "Retry"
    )

    fun serverTimeout() = ErrorInfo(
        title = "Connection Timeout",
        description = "The movie database is taking longer than usual to respond. This might be due to a slow connection.",
        helpText = "🔄 This usually resolves itself in a few moments",
        icon = Icons.Default.CloudOff,
        emoji = "⏱️",
        retryText = "Try Again"
    )

    fun serverError() = ErrorInfo(
        title = "Server Issues",
        description = "The movie database is experiencing technical difficulties. Our team is working to fix this.",
        helpText = "🛠️ Please try again in a few minutes",
        icon = Icons.Default.Error,
        emoji = "🔧",
        retryText = "Retry"
    )

    fun moviesNotFound() = ErrorInfo(
        title = "No Movies Found",
        description = "We couldn't find any movies matching your criteria. Try adjusting your search or filters.",
        helpText = "🔍 Try different keywords or browse popular movies",
        icon = Icons.Default.SearchOff,
        emoji = "🎬",
        retryText = "Browse Popular"
    )

    fun movieDetailsNotFound() = ErrorInfo(
        title = "Movie Details Unavailable",
        description = "We couldn't load the details for this movie. The information might be temporarily unavailable.",
        helpText = "📱 Try going back and selecting the movie again",
        icon = Icons.Default.MovieFilter,
        emoji = "🎭",
        retryText = "Try Again"
    )

    fun rateLimitExceeded() = ErrorInfo(
        title = "Too Many Requests",
        description = "You're browsing movies very quickly! Please wait a moment before making more requests.",
        helpText = "⏳ Wait 30 seconds and try again",
        icon = Icons.Default.Speed,
        emoji = "🚦",
        retryText = "Wait & Retry"
    )

    fun authenticationError() = ErrorInfo(
        title = "Authentication Error",
        description = "There's an issue with the movie database access. This is usually temporary.",
        helpText = "🔐 The app will retry automatically",
        icon = Icons.Default.Lock,
        emoji = "🔑",
        retryText = "Retry"
    )

    fun unknownError(message: String? = null) = ErrorInfo(
        title = "Something Went Wrong",
        description = message ?: "An unexpected error occurred while loading movies. Don't worry, it's not your fault!",
        helpText = "🤝 If this keeps happening, please restart the app",
        icon = Icons.Default.ErrorOutline,
        emoji = "😕",
        retryText = "Try Again"
    )
}