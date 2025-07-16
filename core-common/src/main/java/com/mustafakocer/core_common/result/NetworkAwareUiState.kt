package com.mustafakocer.core_common.result
//
//import com.mustafakocer.core_common.exception.AppException
//
///**
// * TEACHING MOMENT: Network-Aware UI State for Data Preservation
// *
// * PROBLEM SOLVED:
// * ✅ Preserve data when network connection is lost
// * ✅ Show snackbar instead of full error screen when data exists
// * ✅ Still show error screen for initial loads without data
// * ✅ Maintain Clean Architecture principles
// * ✅ Follow SOLID principles
// *
// * ARCHITECTURE DECISION:
// * - Extends UiState concept with network awareness
// * - Separates "has data" from "network available" concerns
// * - Enables different UI behaviors based on data availability
// */
//
///**
// * Enhanced UI State that preserves data during network connectivity issues
// *
// * This sealed class extends the concept of UiState to handle scenarios where:
// * 1. Data is already loaded but network connection is lost → Show snackbar + preserve data
// * 2. No data exists and network connection is lost → Show error screen
// * 3. Data is being refreshed but network is lost → Preserve old data + show snackbar
// */
//sealed class NetworkAwareUiState<out T> {
//
//    /**
//     * Initial state - no operation started yet
//     */
//    object Idle : NetworkAwareUiState<Nothing>()
//
//    /**
//     * Loading state for initial data fetch
//     * @param message Optional loading message
//     */
//    data class InitialLoading(
//        val message: String? = null
//    ) : NetworkAwareUiState<Nothing>()
//
//    /**
//     * Loading state while refreshing existing data
//     * @param currentData The data that's currently displayed
//     * @param message Optional loading message
//     */
//    data class RefreshLoading<T>(
//        val currentData: T,
//        val message: String? = null
//    ) : NetworkAwareUiState<T>()
//
//    /**
//     * Success state with data
//     * @param data The successful result data
//     * @param isOffline Whether we're currently offline (for UI indicators)
//     */
//    data class Success<T>(
//        val data: T,
//        val isOffline: Boolean = false
//    ) : NetworkAwareUiState<T>()
//
//    /**
//     * Success state with preserved data but network connectivity issues
//     * @param data The preserved data from previous successful fetch
//     * @param networkError The network-related exception
//     * @param showSnackbar Whether to show snackbar (managed by ViewModel)
//     */
//    data class SuccessWithNetworkError<T>(
//        val data: T,
//        val networkError: AppException,
//        val showSnackbar: Boolean = true
//    ) : NetworkAwareUiState<T>()
//
//    /**
//     * Error state when no data exists
//     * @param exception The error that occurred
//     */
//    data class Error(
//        val exception: AppException
//    ) : NetworkAwareUiState<Nothing>()
//
//    // ⭐ UTILITY PROPERTIES FOR UI LOGIC
//
//    /**
//     * Check if state is loading (initial or refresh)
//     */
//    val isLoading: Boolean
//        get() = this is InitialLoading || this is RefreshLoading
//
//    /**
//     * Check if state is loading initial data
//     */
//    val isInitialLoading: Boolean
//        get() = this is InitialLoading
//
//    /**
//     * Check if state is refreshing existing data
//     */
//    val isRefreshLoading: Boolean
//        get() = this is RefreshLoading
//
//    /**
//     * Check if state has data available (success or preserved)
//     */
//    val hasData: Boolean
//        get() = this is Success || this is SuccessWithNetworkError || this is RefreshLoading
//
//    /**
//     * Check if state has error without data
//     */
//    val isError: Boolean
//        get() = this is Error
//
//    /**
//     * Check if currently offline with data preserved
//     */
//    val isOfflineWithData: Boolean
//        get() = this is SuccessWithNetworkError
//
//    /**
//     * Get data if available, null otherwise
//     */
//    val dataOrNull: T?
//        get() = when (this) {
//            is Success -> data
//            is SuccessWithNetworkError -> data
//            is RefreshLoading -> currentData
//            else -> null
//        }
//
//    /**
//     * Get exception if error, null otherwise
//     */
//    val exceptionOrNull: AppException?
//        get() = when (this) {
//            is Error -> exception
//            is SuccessWithNetworkError -> networkError
//            else -> null
//        }
//
//    /**
//     * Check if should show network snackbar
//     */
//    val shouldShowNetworkSnackbar: Boolean
//        get() = this is SuccessWithNetworkError && showSnackbar
//}
//
///**
// * TEACHING MOMENT: Extension Functions for NetworkAwareUiState
// *
// * These extensions make working with NetworkAwareUiState more convenient
// * and maintain the functional programming patterns established in UiState
// */
//
///**
// * Transform success data while preserving network state
// */
//inline fun <T, R> NetworkAwareUiState<T>.map(transform: (T) -> R): NetworkAwareUiState<R> {
//    return when (this) {
//        is NetworkAwareUiState.Idle -> NetworkAwareUiState.Idle
//        is NetworkAwareUiState.InitialLoading -> this
//        is NetworkAwareUiState.RefreshLoading -> NetworkAwareUiState.RefreshLoading(
//            currentData = transform(currentData),
//            message = message
//        )
//        is NetworkAwareUiState.Success -> NetworkAwareUiState.Success(
//            data = transform(data),
//            isOffline = isOffline
//        )
//        is NetworkAwareUiState.SuccessWithNetworkError -> NetworkAwareUiState.SuccessWithNetworkError(
//            data = transform(data),
//            networkError = networkError,
//            showSnackbar = showSnackbar
//        )
//        is NetworkAwareUiState.Error -> this
//    }
//}
//
///**
// * Execute action only if state has data
// */
//inline fun <T> NetworkAwareUiState<T>.onData(action: (T) -> Unit): NetworkAwareUiState<T> {
//    dataOrNull?.let { action(it) }
//    return this
//}
//
///**
// * Execute action only if state is success
// */
//inline fun <T> NetworkAwareUiState<T>.onSuccess(action: (T) -> Unit): NetworkAwareUiState<T> {
//    if (this is NetworkAwareUiState.Success) {
//        action(data)
//    }
//    return this
//}
//
///**
// * Execute action only if state is error
// */
//inline fun NetworkAwareUiState<*>.onError(action: (AppException) -> Unit): NetworkAwareUiState<*> {
//    exceptionOrNull?.let { action(it) }
//    return this
//}
//
///**
// * Execute action only if state is loading
// */
//inline fun NetworkAwareUiState<*>.onLoading(action: (String?) -> Unit): NetworkAwareUiState<*> {
//    when (this) {
//        is NetworkAwareUiState.InitialLoading -> action(message)
//        is NetworkAwareUiState.RefreshLoading -> action(message)
//        else -> { /* No action for other states */ }
//    }
//    return this
//}
//
///**
// * Execute action only if state has network error with preserved data
// */
//inline fun <T> NetworkAwareUiState<T>.onNetworkErrorWithData(
//    action: (data: T, error: AppException) -> Unit
//): NetworkAwareUiState<T> {
//    if (this is NetworkAwareUiState.SuccessWithNetworkError) {
//        action(data, networkError)
//    }
//    return this
//}
