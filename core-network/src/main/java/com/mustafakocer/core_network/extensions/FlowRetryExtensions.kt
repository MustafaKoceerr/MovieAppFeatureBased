package com.mustafakocer.core_network.extensions
//
//import com.mustafakocer.core_common.exception.AppException
//import com.mustafakocer.core_common.exception.canRetry
//import com.mustafakocer.core_common.result.NetworkAwareUiState
//import com.mustafakocer.core_common.result.UiState
//import com.mustafakocer.core_network.config.NetworkConfig
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.retryWhen
//import kotlin.math.pow
//
///**
// * UiState flow'ları için yeniden deneme (retry) stratejisi uygular.
// * Sadece yeniden denenebilir olarak işaretlenmiş hatalarda çalışır.
// */
//fun <T> Flow<UiState<T>>.applyRetryStrategy(): Flow<UiState<T>> {
//    return this.retryWhen { cause, attempt ->
//        val shouldRetry = shouldRetry(cause, attempt)
//        if (shouldRetry) {
//            delay(calculateRetryDelay(attempt))
//        }
//        shouldRetry
//    }.catch { throwable ->
//        // Yeniden deneme bittikten sonra hala hata varsa, bunu yakalayıp UiState.Error olarak yay.
//        emit(UiState.Error(throwable.toAppException()))
//    }
//}
//
///**
// * NetworkAwareUiState flow'ları için yeniden deneme (retry) stratejisi uygular.
// */
//@JvmName("applyNetworkAwareRetryStrategy") // <-- YENİ SATIR
//fun <T> Flow<NetworkAwareUiState<T>>.applyRetryStrategy(): Flow<NetworkAwareUiState<T>> {
//    return this.retryWhen { cause, attempt ->
//        val shouldRetry = shouldRetry(cause, attempt)
//        if (shouldRetry) {
//            delay(calculateRetryDelay(attempt))
//        }
//        shouldRetry
//    }.catch { throwable ->
//        // Yeniden deneme bittikten sonra hala hata varsa, bunu yakalayıp NetworkAwareUiState.Error olarak yay.
//        emit(NetworkAwareUiState.Error(throwable.toAppException()))
//    }
//}
//
///**
// * Yeniden deneme işleminin yapılıp yapılmayacağına karar veren merkezi mantık.
// */
//private fun shouldRetry(cause: Throwable, attempt: Long): Boolean {
//    return when {
//        attempt >= NetworkConfig.MAX_RETRY_ATTEMPTS -> false
//        else -> cause.toAppException().canRetry
//    }
//}
//
///**
// * Üstel geri çekilme (exponential backoff) ile gecikme süresini hesaplar.
// * Örnek: 1000ms, 2000ms, 4000ms...
// */
//private fun calculateRetryDelay(attempt: Long): Long {
//    return (NetworkConfig.RETRY_DELAY_MS *
//            NetworkConfig.BACKOFF_MULTIPLIER.pow(attempt.toDouble())).toLong()
//}
//
///**
// * Herhangi bir Throwable'ı standart bir AppException'a dönüştüren yardımcı fonksiyon.
// */
//private fun Throwable.toAppException(): AppException {
//    return this as? AppException ?: AppException.UnknownException(originalCause = this)
//}