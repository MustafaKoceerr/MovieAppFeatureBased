package com.mustafakocer.core_network.error

import com.mustafakocer.core_common.exception.AppException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * Ağ katmanına özgü hataları, projenin anladığı dilde (:core-domain'deki AppException)'a çeviren nesne
 */
object ErrorMapper {

    // Bu fonksiyon, HttpException ve IOException gibi istisnaları ele alır.
    fun mapThrowableToAppException(throwable: Throwable): AppException {
        return when (throwable) {
            is AppException -> throwable
            is HttpException -> {
                // HTTP hata koduna göre spesifik bir AppException döndür.
                when (throwable.code()) {
                    401 -> AppException.Api.Unauthorized(throwable)
                    404 -> AppException.Api.NotFound(throwable)
                    in 500..599 -> AppException.Api.ServerError(throwable, throwable.code())
                    else -> AppException.Unknown(throwable)
                }
            }

            is IOException -> AppException.Network.NoInternet(throwable)
            // SocketTimeoutException da bir IOException'dır, bu yüzden yukarıdaki dal onu yakalar.
            else -> AppException.Unknown(throwable)
        }
    }

    // Bu fonksiyon, başarılı olmayan ama istisna fırlatmayan Response'ları ele alır.
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