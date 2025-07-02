package com.mustafakocer.core_ui.component.error

import com.mustafakocer.core_common.exception.AppException

/**
 * Extension functions to map AppException to UI-friendly error handling
 *
 * HYBRID APPROACH:
 * ✅ Global infrastructure errors → GeneralErrorType
 * ✅ Contextual business errors → Handle in ViewModel
 * ✅ Clean separation of concerns
 */

/**
 * Map AppException to GeneralErrorType if it's a global infrastructure error.
 * Returns null for contextual errors that should be handled in ViewModel.
 */

fun AppException.toGeneralErrorTypeOrNull(): GeneralErrorType? {
    return when (this) {
        // Network infrastructure errors (always global)
        is AppException.NetworkException.NoInternetConnection -> GeneralErrorType.NO_INTERNET
        is AppException.NetworkException.ServerTimeout -> GeneralErrorType.SERVER_TIMEOUT
        is AppException.NetworkException.ServerUnreachable -> GeneralErrorType.SERVER_UNREACHABLE

        // Server infrastructure errors (always global)
        is AppException.ApiException.ServerError -> GeneralErrorType.SERVER_ERROR

        // Rate limiting (always global)
        is AppException.ApiException.TooManyRequests -> GeneralErrorType.RATE_LIMITED

        // These are CONTEXTUAL - return null to handle in ViewModel:
        // - 404 NotFound (context-dependent)
        // - 400 BadRequest (validation context-dependent)
        // - 401 Unauthorized (auth context-dependent)
        // - 403 Forbidden (permission context-dependent)
        // - DataException.* (feature-dependent)
        else -> null
    }
}

/**
 * Convenience method: Get ErrorInfo for global errors, or fallback message for contextual ones
 */
fun AppException.toErrorInfoOrFallback(): ErrorInfo {
    return toGeneralErrorTypeOrNull()?.let { generalType ->
        // Global infrastructure error - use factory
        GenericErrorMessageFactory.createFrom(generalType, this.userMessage)
    } ?: run {
        // Contextual error - use generic unknown error with message
        GenericErrorMessageFactory.unknownError(this.userMessage)
    }
}

/**
 * Check if this exception represents a global infrastructure error
 */
fun AppException.isGlobalInfrastructureError(): Boolean {
    return toGeneralErrorTypeOrNull() != null
}

/**
 * Check if this exception requires contextual handling in ViewModel
 */
fun AppException.requiresContextualHandling(): Boolean {
    return !isGlobalInfrastructureError()
}