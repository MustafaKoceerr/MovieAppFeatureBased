package com.mustafakocer.core_domain.exception

import java.io.IOException

/**
 * Defines a type-safe hierarchy for all custom exceptions within the application.
 *
 * Architectural Note:
 * This sealed class creates a single, well-defined contract for error handling across all layers.
 * It represents *what* went wrong technically, not *what* message to show the user. The UI layer
 * is responsible for mapping these exceptions to user-friendly, localized messages.
 */
sealed class AppException(
    open val technicalMessage: String? = null,
    override val cause: Throwable? = null,
) : Exception(technicalMessage, cause) {

    /**
     * Represents infrastructure-level network problems.
     */
    sealed class Network(
        technicalMessage: String?,
        cause: Throwable? = null,
    ) : AppException(technicalMessage, cause) {
        data class NoInternet(override val cause: Throwable? = null) :
            Network("No internet connection", cause)

        data class Timeout(override val cause: Throwable? = null) :
            Network("Request timed out", cause)
    }

    /**
     * Represents errors originating from the server's HTTP responses.
     */
    sealed class Api(
        val httpCode: Int,
        technicalMessage: String?,
        cause: Throwable? = null,
    ) : AppException(technicalMessage, cause) {
        data class Unauthorized(override val cause: Throwable? = null) :
            Api(401, "Unauthorized", cause)

        data class NotFound(override val cause: Throwable? = null) :
            Api(404, "Not Found", cause)

        data class ServerError(override val cause: Throwable? = null, val code: Int) :
            Api(code, "Server Error", cause)
    }

    /**
     * Represents errors during data processing, such as parsing or unexpected empty responses.
     */
    sealed class Data(
        technicalMessage: String?,
        cause: Throwable? = null,
    ) : AppException(technicalMessage, cause) {
        data class Parse(override val cause: Throwable? = null) :
            Data("Data parsing error", cause)

        data object EmptyResponse : Data("Empty response body")
    }

    /**
     * A catch-all for any exceptions not covered by the specific types above.
     */
    data class Unknown(override val cause: Throwable? = null) :
        AppException("An unknown error occurred", cause)
}

/**
 * Converts a generic [Throwable] into a more specific [AppException].
 *
 * Why this is an extension function:
 * It provides a centralized and reusable mechanism to sanitize exceptions caught in lower
 * layers (e.g., Repositories) before they are passed up to the domain or UI layers. This
 * promotes consistency in error handling.
 *
 * @return The corresponding [AppException] subclass.
 */
fun Throwable.toAppException(): AppException {
    return when (this) {
        is AppException -> this // Avoid re-wrapping if it's already our custom type.
        is IOException -> AppException.Network.NoInternet(this)
        // Future platform-specific or library exceptions can be mapped here.
        else -> AppException.Unknown(this)
    }
}