package com.mustafakocer.core_network.utils

import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_network.error.applyRetryStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

// # Utility functions

fun <T> Flow<UiState<T>>.withNetworkConfig(): Flow<UiState<T>> {
    return this.applyRetryStrategy()
}

fun <T> Flow<UiState<T>>.handleOffline(
    fallbackData: T? = null,
): Flow<UiState<T>> {
    return this.catch { throwable ->
        if (fallbackData == null) throw throwable
        // null değilse
        emit(UiState.Success(fallbackData))
    }
}