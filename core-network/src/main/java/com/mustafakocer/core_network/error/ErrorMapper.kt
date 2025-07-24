package com.mustafakocer.core_network.error

import com.mustafakocer.core_domain.exception.AppException
import java.io.IOException
import retrofit2.HttpException
import retrofit2.Response

/**
 * A singleton object responsible for mapping network-layer errors to domain-specific [AppException]s.
 *
 * Architectural Note:
 * This mapper acts as a crucial boundary between the network layer and the rest of the application.
 * It translates raw, platform-specific exceptions (like Retrofit's [HttpException] or Java's
 * [IOException]) into the well-defined, sealed hierarchy of [AppException]. This ensures that
 * higher-level components, like repositories and ViewModels, are decoupled from the network
 * implementation details and can handle errors in a consistent, type-safe manner.
 */
object ErrorMapper {

    fun mapThrowableToAppException(throwable: Throwable): AppException {
        return when (throwable) {
            is AppException -> throwable // If it's already our type, pass it through.
            is HttpException -> {
                // Map specific HTTP error codes to our defined API exceptions.
                when (throwable.code()) {
                    401 -> AppException.Api.Unauthorized(throwable)
                    404 -> AppException.Api.NotFound(throwable)
                    in 500..599 -> AppException.Api.ServerError(throwable, throwable.code())
                    else -> AppException.Unknown(throwable)
                }
            }
            // IOException is a common parent for network issues like no connectivity
            // or request timeouts.
            is IOException -> AppException.Network.NoInternet(throwable)
            else -> AppException.Unknown(throwable)
        }
    }


    fun mapHttpErrorResponseToAppException(response: Response<*>): AppException {
        val code = response.code()
        return when (code) {
            401 -> AppException.Api.Unauthorized()
            404 -> AppException.Api.NotFound()
            in 500..599 -> AppException.Api.ServerError(code = code)
            else -> AppException.Unknown()
        }
    }
}