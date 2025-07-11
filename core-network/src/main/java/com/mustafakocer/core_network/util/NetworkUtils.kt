package com.mustafakocer.core_network.util

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.exception.toAppException
import com.mustafakocer.core_common.result.NetworkAwareUiState
import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.core_network.error.ErrorMapper
import com.mustafakocer.core_network.extensions.applyRetryStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

/**
 * Execute API call safely with comprehensive error handling
 *
 * @param apiCall The suspend function that makes the API call
 * @return Flow<UiState<T>> with proper error mapping
 */

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> Response<T>,
    loadingMessage: String? =null,

    ): Flow<UiState<T>> = flow {

    emit(UiState.Loading(loadingMessage))

    try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
            body?.let { emit(UiState.Success(body)) }
            if (body == null) {
                emit(UiState.Error(AppException.DataException.EmptyResponse))
            }
        } else {
            val appException = ErrorMapper.mapHttpError(response)
            emit(UiState.Error(appException))
        }
    } catch (exception: Exception) {
        val appException = ErrorMapper.mapThrowable(exception)
        emit(UiState.Error(appException))
    }
}.flowOn(Dispatchers.IO)


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



/**
 * Network-aware flow that initializes once and only refreshes on connectivity changes
 *
 * PATTERN: Initialize-Once Strategy
 * - Loads data once initially
 * - Only refreshes when connectivity is restored
 * - Ideal for details screens where you don't want constant reloading
 *
 * @param R The type of the domain model
 * @param networkMonitor The connectivity monitor
 * @param dataFetcher The suspend block that fetches data from API
 * @return Flow<NetworkAwareUiState<R>> Reactive flow with initialization strategy
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <R> createInitializeOnceNetworkAwareFlow(
    networkMonitor: NetworkConnectivityMonitor,
    dataFetcher: suspend () -> R
): Flow<NetworkAwareUiState<R>> {

    var lastSuccessfulData: R? = null
    var hasInitialized = false

    return networkMonitor.observeConnectivity()
        .distinctUntilChanged()
        .flatMapLatest { connectionState ->

            // Only fetch if not initialized or if connectivity was restored
            val shouldFetch = !hasInitialized ||
                    (connectionState.isConnected && lastSuccessfulData != null)

            if (connectionState.isConnected && shouldFetch) {
                flow {
                    val loadingState = if (lastSuccessfulData != null) {
                        NetworkAwareUiState.RefreshLoading(
                            currentData = lastSuccessfulData!!,
                            message = "Refreshing..."
                        )
                    } else {
                        NetworkAwareUiState.InitialLoading("Loading...")
                    }

                    emit(loadingState)

                    try {
                        val result = dataFetcher()
                        lastSuccessfulData = result
                        hasInitialized = true
                        emit(NetworkAwareUiState.Success(result))

                    } catch (e: Exception) {
                        val appException = e.toAppException()
                        hasInitialized = true // Mark as initialized even on error

                        if (lastSuccessfulData != null) {
                            emit(
                                NetworkAwareUiState.SuccessWithNetworkError(
                                    data = lastSuccessfulData!!,
                                    networkError = appException,
                                    showSnackbar = true
                                )
                            )
                        } else {
                            emit(NetworkAwareUiState.Error(appException))
                        }
                    }
                }.applyRetryStrategy()

            } else if (!connectionState.isConnected) {
                if (lastSuccessfulData != null) {
                    flowOf(
                        NetworkAwareUiState.SuccessWithNetworkError(
                            data = lastSuccessfulData!!,
                            networkError = AppException.NetworkException.NoInternetConnection(
                                userMessage = "No internet connection"
                            ),
                            showSnackbar = true
                        )
                    )
                } else {
                    flowOf(
                        NetworkAwareUiState.Error(
                            AppException.NetworkException.NoInternetConnection(
                                userMessage = "No internet connection. Please check your network and try again."
                            )
                        )
                    )
                }
            } else {
                // Already initialized and connected - emit current data
                if (lastSuccessfulData != null) {
                    flowOf(NetworkAwareUiState.Success(lastSuccessfulData!!))
                } else {
                    flowOf(NetworkAwareUiState.Idle) // This shouldn't happen but failsafe
                }
            }
        }
}
