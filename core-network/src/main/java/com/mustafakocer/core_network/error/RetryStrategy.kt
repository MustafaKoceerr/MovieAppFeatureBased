package com.mustafakocer.core_network.error

import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.exception.canRetry
import com.mustafakocer.core_network.config.NetworkConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlin.math.pow

// # Automatic retry logic

fun <T> Flow<UiState<T>>.applyRetryStrategy(): Flow<UiState<T>> {
    return this.retryWhen { cause, attempt ->
        val shouldRetry = when {
            attempt >= NetworkConfig.MAX_RETRY_ATTEMPTS -> false
            cause is Exception -> {
                val appException =
                    cause as? AppException ?: AppException.UnknownException(originalCause = cause)
                appException.canRetry
            }

            else -> false
        }

        if (shouldRetry) {
            val delayMs = calculateRetryDelay(attempt.toInt())
            delay(delayMs)
            true
        } else {
            false
        }
    }.catch { throwable ->
        val appException =
            throwable as? AppException ?: AppException.UnknownException(originalCause = throwable)
        emit(UiState.Error(appException))
    }
}

internal fun calculateRetryDelay(attempt: Int): Long {
    return (NetworkConfig.RETRY_DELAY_MS *
            NetworkConfig.BACKOFF_MULTIPLIER.pow(attempt.toDouble())).toLong()
}

/**
 * Wrapper for UiState to make it retryable
 */
internal class UiStateWrapper<T>(private val state: UiState<T>) : RetryableState<T> {
    override fun toErrorState(exception: AppException): RetryableState<T> {
        return UiStateWrapper(UiState.Error(exception))
    }

    override val isRetryable: Boolean
        get() = state is UiState.Error && state.exception.canRetry
}