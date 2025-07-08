// core-network/src/main/java/com/mustafakocer/core_network/extensions/NetworkAwareFlowExtensions.kt
package com.mustafakocer.core_network.extensions

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.exception.toAppException
import com.mustafakocer.core_common.result.NetworkAwareUiState
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.core_network.error.applyRetryStrategy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * TEACHING MOMENT: Network-Aware Flow Extensions for Data Preservation
 *
 * ARCHITECTURE PRINCIPLE: Single Responsibility + Open/Closed
 * ✅ Each function has ONE clear responsibility
 * ✅ Open for extension (can add more strategies)
 * ✅ Closed for modification (existing behavior is stable)
 * ✅ Repository layer concern - UI knows nothing about network logic
 * ✅ Framework-agnostic (works with any HTTP client)
 */

/**
 * Creates a network-aware Flow that preserves data during connectivity issues
 *
 * BEHAVIOR:
 * - First time with no data + no internet → Error state
 * - Has data + no internet → Preserve data + show snackbar
 * - Internet comes back → Refresh data automatically
 * - Failed refresh with existing data → Preserve data + show error snackbar
 *
 * @param R The type of the domain model
 * @param networkMonitor The connectivity monitor
 * @param dataFetcher The suspend block that fetches data from API
 * @return Flow<NetworkAwareUiState<R>> Reactive flow with data preservation
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <R> createNetworkAwareFlow(
    networkMonitor: NetworkConnectivityMonitor,
    dataFetcher: suspend () -> R
): Flow<NetworkAwareUiState<R>> {

    // STATE HOLDER: Keep track of last successful data
    var lastSuccessfulData: R? = null

    return networkMonitor.observeConnectivity()
        .distinctUntilChanged() // Only emit when connectivity actually changes
        .flatMapLatest { connectionState ->

            if (connectionState.isConnected) {
                // SCENARIO 1: Internet is available - fetch data
                flow {
                    // Determine loading state based on existing data
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
                        lastSuccessfulData = result // Cache successful result
                        emit(NetworkAwareUiState.Success(result))

                    } catch (e: Exception) {
                        val appException = e.toAppException()

                        if (lastSuccessfulData != null) {
                            // HAS DATA: Show preserved data with error indication
                            emit(
                                NetworkAwareUiState.SuccessWithNetworkError(
                                    data = lastSuccessfulData!!,
                                    networkError = appException,
                                    showSnackbar = true
                                )
                            )
                        } else {
                            // NO DATA: Show error screen
                            emit(NetworkAwareUiState.Error(appException))
                        }
                    }
                }.applyRetryStrategy() // Apply retry logic for network calls

            } else {
                // SCENARIO 2: No internet connection
                if (lastSuccessfulData != null) {
                    // HAS DATA: Preserve it and show connectivity snackbar
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
                    // NO DATA: Show error screen
                    flowOf(
                        NetworkAwareUiState.Error(
                            AppException.NetworkException.NoInternetConnection(
                                userMessage = "No internet connection. Please check your network and try again."
                            )
                        )
                    )
                }
            }
        }
}

/**
 * Alternative: Network-aware flow with manual refresh capability
 *
 * PATTERN: Command Query Responsibility Segregation (CQRS)
 * - Separates read operations (observeData) from write operations (refresh)
 * - Useful when you need explicit refresh control
 *
 * @param R The type of the domain model
 * @param networkMonitor The connectivity monitor
 * @param refreshTrigger Flow that triggers refresh operations
 * @param dataFetcher The suspend block that fetches data from API
 * @return Flow<NetworkAwareUiState<R>> Reactive flow with manual refresh
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <R> createNetworkAwareFlowWithRefresh(
    networkMonitor: NetworkConnectivityMonitor,
    refreshTrigger: Flow<Unit>, // External trigger for refresh
    dataFetcher: suspend () -> R
): Flow<NetworkAwareUiState<R>> {

    var lastSuccessfulData: R? = null

    return refreshTrigger
        .flatMapLatest {
            networkMonitor.observeConnectivity()
                .distinctUntilChanged()
                .flatMapLatest { connectionState ->

                    if (connectionState.isConnected) {
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
                                emit(NetworkAwareUiState.Success(result))

                            } catch (e: Exception) {
                                val appException = e.toAppException()

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

                    } else {
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
                    }
                }
        }
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

/**
 * TEACHING MOMENT: Why Multiple Strategies?
 *
 * Different screens have different requirements:
 *
 * 1. createNetworkAwareFlow() - Auto-refresh on connectivity
 *    Use for: List screens, home screens, frequently updated content
 *
 * 2. createNetworkAwareFlowWithRefresh() - Manual refresh control
 *    Use for: Pull-to-refresh scenarios, user-controlled updates
 *
 * 3. createInitializeOnceNetworkAwareFlow() - Initialize once strategy
 *    Use for: Detail screens, settings, content that doesn't change frequently
 */