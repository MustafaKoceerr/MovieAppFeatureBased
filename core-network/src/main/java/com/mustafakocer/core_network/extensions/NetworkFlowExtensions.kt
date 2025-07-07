package com.mustafakocer.core_network.extensions

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.exception.toAppException
import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.core_network.error.applyRetryStrategy
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * TEACHING MOMENT: Framework-Agnostic Reactive Network Flow Extensions
 *
 * Bu dosya network ile ilgili Flow extension'larını içerir:
 * ✅ Framework-agnostic (Retrofit, Ktor, her ne olursa olsun)
 * ✅ Clean architecture principles
 * ✅ Repository sorumlulukları doğru yerde
 * ✅ Test edilebilir
 * ✅ Esnek ve yeniden kullanılabilir
 */

/**
 * Creates a reactive Flow that handles network connectivity, loading, success, and error states.
 * This function is completely decoupled from any HTTP client (Retrofit, Ktor, etc.)
 *
 * @param R The type of the final domain model
 * @param networkMonitor The connectivity monitor
 * @param block The suspend block that performs data fetching and mapping
 *              Should return domain model (R) on success or throw Exception on failure
 * @return Flow<UiState<R>> Reactive flow with automatic connectivity handling
 */

@OptIn(ExperimentalCoroutinesApi::class)
fun <R> createNetworkFlow(
    networkMonitor: NetworkConnectivityMonitor,
    block: suspend () -> R, // ✅ CLEAN: Artık sadece domain model döndürür
): Flow<UiState<R>> {
    return networkMonitor.observeConnectivity()
        .distinctUntilChanged()
        .flatMapLatest { connectionState ->
            if (connectionState.isConnected) {
                // Durum 1: Internet Var- Verilen bloğu çalıştırır
                flow {
                    emit(UiState.Loading("Loading..."))
                    try {
                        val result = block() // Kullanıcının verdiği kod bloğunu çağır
                        emit(UiState.Success(result))
                    } catch (e: Exception) {
                        // Hata durumunda generic hata yakalayıcımızla sarmala
                        emit(UiState.Error(e.toAppException()))
                    }
                }.applyRetryStrategy() // Retry mekanizması hala geçerli!
            } else {
                // Durum 2: Internet yok- Doğrudan hata durumu yay
                flowOf(
                    UiState.Error(
                        AppException.NetworkException.NoInternetConnection(
                            userMessage = "No internet connection. Please check your connection and try again."
                        )
                    )
                )
            }
        }.catch { e ->
            emit(UiState.Error(AppException.UnknownException(originalCause = e)))
        }
}

// Future: Buraya başka network extension'ları eklenebilir
// fun <R> createNetworkFlowWithCache(...)
// fun <R> createNetworkFlowWithPagination(...)