package com.mustafakocer.core_domain.exception

import java.io.IOException


/**
 * Uygulama genelindeki tüm hataları temsil eden, tip-güvenli hiyerarşi.
 * Bu sınıf, hatanın "ne olduğunu" tanımlar, kullanıcıya "ne gösterileceğini" değil.
 */

sealed class AppException(
    open val technicalMessage: String? = null,
    override val cause: Throwable? = null,
) : Exception(technicalMessage, cause) {

    // Ağ bağlantısı gibi altyapısal sorunlar.
    sealed class Network(
        technicalMessage: String?,
        cause: Throwable? = null,
    ) : AppException(technicalMessage, cause) {
        data class NoInternet(override val cause: Throwable? = null) :
            Network("No internet connection", cause)

        data class Timeout(override val cause: Throwable? = null) :
            Network("Request timed out", cause)
    }

    // Sunucudan gelen HTTP hata kodları
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

    // Veri işleme (parsing) veya beklenmedik boş yanıtlar.
    sealed class Data(
        technicalMessage: String?,
        cause: Throwable? = null,
    ) : AppException(technicalMessage, cause) {
        data class Parse(override val cause: Throwable? = null) :
            Data("Data parsing error", cause)

        data object EmptyResponse : Data("Empty response body")
    }

    // Yukarıdaki kategorilere girmeyen diğer tüm hatalar.
    data class Unknown(override val cause: Throwable? = null) :
        AppException("An unknown error occurred", cause)
}

// Throwable'ı bizim temiz AppException hiyerarşimize çeviren yardımcı fonksiyon
fun Throwable.toAppException(): AppException {
    return when (this) {
        is AppException -> this
        is IOException -> AppException.Network.NoInternet(this)
        // Diğer spesifik Throwable'ları buraya ekleyebiliriz.
        else -> AppException.Unknown(this)
    }
}