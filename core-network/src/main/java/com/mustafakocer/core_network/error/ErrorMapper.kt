package com.mustafakocer.core_network.error

import com.mustafakocer.core_common.exception.AppException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * TEACHING MOMENT: Centralized Error Mapping
 *
 * Bu class tüm HTTP response'ları ve exception'ları
 * type-safe AppException'lara çevirir
 */
object ErrorMapper {

    /**
     * Map HTTP errors to AppException
     */
    fun mapHttpError(response: Response<*>): AppException {
        return when (response.code()) {
            400 -> AppException.ApiException.BadRequest(
                technicalMessage = "HTTP 400: ${response.message()}"
            )

            401 -> AppException.ApiException.Unauthorized(
                technicalMessage = "HTTP 401: ${response.message()}"
            )

            403 -> AppException.ApiException.Forbidden(
                technicalMessage = "HTTP 403: ${response.message()}"
            )

            404 -> AppException.ApiException.NotFound(
                technicalMessage = "HTTP 404: ${response.message()}"
            )

            429 -> {
                val retryAfter = parseRetryAfter(response)
                AppException.ApiException.TooManyRequests(
                    retryAfterSeconds = retryAfter
                )
            }

            in 500..599 -> AppException.ApiException.ServerError(
                httpCode = response.code(),
                technicalMessage = "HTTP ${response.code()}: ${response.message()}"
            )

            else -> AppException.ApiException.ServerError(
                httpCode = response.code(),
                userMessage = "Unexpected error occurred",
                technicalMessage = "HTTP ${response.code()}: ${response.message()}"
            )
        }
    }

    /**
     * Map generic throwables to AppException
     */
    fun mapThrowable(throwable: Throwable): AppException {
        return when (throwable) {
            is AppException -> throwable

            is UnknownHostException -> AppException.NetworkException.ServerUnreachable(
                originalCause = throwable
            )

            is SocketTimeoutException -> AppException.NetworkException.ServerTimeout(
                originalCause = throwable
            )

            is IOException -> AppException.NetworkException.NoInternetConnection(
                originalCause = throwable
            )

            else -> AppException.UnknownException(
                technicalMessage = throwable.message,
                originalCause = throwable
            )
        }
    }

    /**
     * Parse Retry-After header from 429 responses
     */
    private fun parseRetryAfter(response: Response<*>): Int? {
        return try {
            response.headers()["Retry-After"]?.toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }
}