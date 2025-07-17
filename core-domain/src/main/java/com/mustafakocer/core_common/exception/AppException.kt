package com.mustafakocer.core_common.exception

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class AppException(
    open val userMessage: String,
    open val technicalMessage: String? = null,
    open val errorCode: String? = null,
    open val originalCause: Throwable? = null,
) : Exception(userMessage, originalCause) {

    /**
     * Network related exceptions
     */
    sealed class NetworkException(
        userMessage: String,
        technicalMessage: String? = null,
        originalCause: Throwable? = null,
    ) : AppException(userMessage, technicalMessage, "NETWORK", originalCause) {

        data class NoInternetConnection(
            override val userMessage: String = "Please check your internet connection",
            override val originalCause: Throwable? = null,
        ) : NetworkException(userMessage, "No internet connection", originalCause)

        data class ServerTimeout(
            override val userMessage: String = "Request timed out. Please try again.",
            override val originalCause: Throwable? = null,
        ) : NetworkException(userMessage, "Server timeout", originalCause)

        data class ServerUnreachable(
            override val userMessage: String = "Cannot reach server. Please try again later.",
            override val originalCause: Throwable? = null,
        ) : NetworkException(userMessage, "Server unreachable", originalCause)
    }

    /**
     * HTTP/API related exceptions
     */
    sealed class ApiException(
        userMessage: String,
        technicalMessage: String? = null,
        open val httpCode: Int,
        originalCause: Throwable? = null,
    ) : AppException(userMessage, technicalMessage, "API_$httpCode", originalCause) {

        data class BadRequest(
            override val userMessage: String = "Invalid request data",
            override val technicalMessage: String? = null,
        ) : ApiException(userMessage, technicalMessage, 400)

        data class Unauthorized(
            override val userMessage: String = "Session expired. Please login again.",
            override val technicalMessage: String? = "Unauthorized access",
            val shouldRedirectToLogin: Boolean = true,
        ) : ApiException(userMessage, technicalMessage, 401)

        data class Forbidden(
            override val userMessage: String = "Access denied",
            override val technicalMessage: String? = null,
        ) : ApiException(userMessage, technicalMessage, 403)

        data class NotFound(
            override val userMessage: String = "Content not found",
            override val technicalMessage: String? = null,
        ) : ApiException(userMessage, technicalMessage, 404)

        data class TooManyRequests(
            override val userMessage: String = "Too many requests. Please wait a moment.",
            val retryAfterSeconds: Int? = null,
        ) : ApiException(userMessage, "Rate limited", 429)

        data class ServerError(
            override val userMessage: String = "Server is having issues. Please try again later.",
            override val technicalMessage: String? = null,
            override val httpCode: Int,
        ) : ApiException(userMessage, technicalMessage, httpCode)
    }

    /**
     * Data parsing/validation exceptions
     */
    sealed class DataException(
        userMessage: String,
        technicalMessage: String? = null,
        originalCause: Throwable? = null,
    ) : AppException(userMessage, technicalMessage, "DATA", originalCause) {

        data class ParseError(
            override val userMessage: String = "Invalid data received",
            override val technicalMessage: String? = null,
            override val originalCause: Throwable? = null,
        ) : DataException(userMessage, technicalMessage, originalCause)

        data class ValidationError(
            override val userMessage: String,
            val field: String? = null,
            override val technicalMessage: String? = null,
        ) : DataException(userMessage, technicalMessage)

        object EmptyResponse : DataException(
            "No data received from server",
            "Empty response body"
        )
    }

    /**
     * Unknown/unexpected exceptions
     */
    data class UnknownException(
        override val userMessage: String = "An unexpected error occurred",
        override val technicalMessage: String? = null,
        override val originalCause: Throwable? = null,
    ) : AppException(userMessage, technicalMessage, "UNKNOWN", originalCause)
}


/**
 * TEACHING MOMENT: Exception Extension Functions
 *
 * Bu extension'lar exception handling'i kolaylaştırır
 */

/**
 * Check if exception suggests retry is possible
 */
val AppException.canRetry: Boolean
    get() = when (this) {
        is AppException.NetworkException -> true
        is AppException.ApiException.TooManyRequests -> true
        is AppException.ApiException.ServerError -> httpCode >= 500
        else -> false
    }

/**
 * Check if exception suggests user should check internet
 */
val AppException.shouldCheckInternet: Boolean
    get() = this is AppException.NetworkException


/**
 * Check if exception suggests user should login again
 */
val AppException.shouldRedirectToLogin: Boolean
    get() = when (this) {
        is AppException.ApiException.Unauthorized -> shouldRedirectToLogin
        else -> false
    }

/**
 * Get retry delay in seconds (if applicable)
 */
val AppException.retryDelaySeconds: Int?
    get() = when (this) {
        is AppException.ApiException.TooManyRequests -> retryAfterSeconds
        is AppException.NetworkException -> 5 // Default network retry delay
        is AppException.ApiException.ServerError -> 10 // Default server error retry delay
        else -> null
    }

/**
 * Convert generic Throwable to AppException
 */
fun Throwable.toAppException(): AppException {
    return when (this) {
        is AppException -> this
        is UnknownHostException -> AppException.NetworkException.ServerUnreachable(
            originalCause = this
        )

        is SocketTimeoutException -> AppException.NetworkException.ServerTimeout(
            originalCause = this
        )

        is IOException -> AppException.NetworkException.NoInternetConnection(
            originalCause = this
        )

        else -> AppException.UnknownException(
            technicalMessage = this.message,
            originalCause = this
        )
    }
}