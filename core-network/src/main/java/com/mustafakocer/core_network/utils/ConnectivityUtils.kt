package com.mustafakocer.core_network.utils

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import kotlinx.coroutines.flow.FlowCollector

/**
 * GENERIC: Network connectivity utilities for repositories
 *
 * ✅ Generic type support for any data type
 * ✅ Reusable across all repositories
 * ✅ Consistent connectivity handling
 * ✅ DRY principle applied
 */

/**
 * Check network connectivity before making API call
 *
 * @param T The data type for UiState
 * @return true if connected, emits error and returns false if not
 *
 * USAGE:
 * ```
 * override fun getMovies(): Flow<UiState<List<Movie>>> = flow {
 *     if (!checkConnectivity(networkConnectivityMonitor)) return@flow
 *     // Continue with API call...
 * }
 * ```
 */
suspend fun <T> FlowCollector<UiState<T>>.checkConnectivity(
    networkConnectivityMonitor: NetworkConnectivityMonitor,
): Boolean {
    val connectionState = networkConnectivityMonitor.getCurrentConnectionState()
    if (!connectionState.isConnected) {
        emit(UiState.Error(AppException.NetworkException.NoInternetConnection()))
        return false
    }
    return true
}


/**
 * Alternative: Extension function on NetworkConnectivityMonitor
 *
 * @param T The data type for UiState
 * @return true if connected, emits error and returns false if not
 *
 * USAGE:
 * ```
 * override fun getMovies(): Flow<UiState<List<Movie>>> = flow {
 *     if (!networkConnectivityMonitor.checkConnectivityOrEmitError(this)) return@flow
 *     // Continue with API call...
 * }
 * ```
 */
suspend fun <T> NetworkConnectivityMonitor.checkConnectivityOrEmitError(
    collector: FlowCollector<UiState<T>>,
): Boolean {
    val connectionState = getCurrentConnectionState()
    if (!connectionState.isConnected) {
        collector.emit(UiState.Error(AppException.NetworkException.NoInternetConnection()))
        return false
    }
    return true
}

/**
 * Most concise: Direct extension on FlowCollector with implicit NetworkConnectivityMonitor
 *
 * @param T The data type for UiState
 * @param networkConnectivityMonitor The connectivity monitor instance
 * @return true if connected, emits error and returns false if not
 *
 * USAGE:
 * ```
 * override fun getMovies(): Flow<UiState<List<Movie>>> = flow {
 *     if (!ensureConnected(networkConnectivityMonitor)) return@flow
 *     // Continue with API call...
 * }
 * ```
 */
suspend fun <T> FlowCollector<UiState<T>>.ensureConnected(
    networkConnectivityMonitor: NetworkConnectivityMonitor,
): Boolean {
    val connectionState = networkConnectivityMonitor.getCurrentConnectionState()
    if (!connectionState.isConnected) {
        emit(UiState.Error(AppException.NetworkException.NoInternetConnection()))
        return false
    }
    return true
}