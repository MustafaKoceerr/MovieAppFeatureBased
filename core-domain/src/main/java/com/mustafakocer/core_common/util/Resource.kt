package com.mustafakocer.core_common.util

import com.mustafakocer.core_common.exception.AppException

/**
 * Bir veri isteğinin durumunu temsil eden sarmalayıcı sınıf.
 * Bu, UI katmanının yükleme, başarı ve hata durumlarını net bir şekilde
 * yönetmesini sağlar.
 */

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()

    /**
     * Verinin başarıyla alındığı durumu temsil eder.
     * @param data Alınan veri.
     */
    data class Success<out T>(val data: T) : Resource<T>()

    /**
     * Bir hata oluştuğu durumu temsil eder.
     * @param exception Oluşan hatanın detaylarını içeren AppException.
     */
    data class Error(val exception: AppException) : Resource<Nothing>()
}