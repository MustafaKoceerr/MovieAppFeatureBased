//package com.mustafakocer.core_common.result
//
//import com.mustafakocer.core_common.exception.AppException
//
//
///**
// * TEACHING MOMENT: UiState vs Resource Pattern
// *
// * ESKI YAKLAŞIM (Resource):
// * sealed class Resource<T> {
// *     object Loading : Resource<Nothing>()
// *     data class Success<T>(val data: T) : Resource<T>()
// *     data class Error(val message: String) : Resource<Nothing>()
// * }
// *
// * YENİ YAKLAŞIM (UiState):
// * ✅ Better error handling with exception types
// * ✅ More flexible loading states
// * ✅ Easier testing
// * ✅ Better IDE support
// */
//
///**
// * Modern UI State representation
// *
// * Generic state container that represents the state of any UI operation
// */
//sealed class UiState<out T> {
//    /**
//     * Initial/Idle state - no operation started yet
//     */
//    object Idle : UiState<Nothing>()
//
//    /**
//     * Loading state - operation in progress
//     * @param message Optional loading message for better UX
//     */
//    data class Loading(
//        val message: String? = null,
//    ) : UiState<Nothing>()
//
//    /**
//     * Success state with data
//     * @param data The successful result data
//     */
//    data class Success<T>(
//        val data: T,
//    ) : UiState<T>()
//
//    /**
//     * Error state with detailed exception information
//     * @param exception The AppException containing error details
//     */
//    data class Error(
//        val exception: AppException,
//    ) : UiState<Nothing>()
//
//    // ⭐ UTILITY EXTENSIONS FOR EASIER USAGE
//
//    /**
//     * Check if state is loading
//     */
//    val isLoading: Boolean
//        get() = this is Loading
//
//    /**
//     * Check if state has data
//     */
//    val isSuccess: Boolean
//        get() = this is Success
//
//    /**
//     * Check if state has error
//     */
//    val isError: Boolean
//        get() = this is Error
//
//    /**
//     * Get data if success, null otherwise
//     */
//    val dataOrNull: T?
//        get() = if (this is Success) data else null
//
//    /**
//     * Get exception if error, null otherwise
//     */
//    val exceptionOrNull: AppException?
//        get() = if (this is Error) exception else null
//}
//
///**
// * TEACHING MOMENT: Extension Functions for UiState
// *
// * Bu extension'lar UiState kullanımını kolaylaştırır:
// */
//
///**
// * Transform success data while preserving other states
// */
//inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> {
//    return when (this) {
//        is UiState.Idle -> UiState.Idle
//        is UiState.Loading -> this
//        is UiState.Success -> UiState.Success(transform(data))
//        is UiState.Error -> this
//    }
//}
//
///**
// * Execute action only if state is success
// */
//inline fun <T> UiState<T>.onSuccess(action: (T) -> Unit): UiState<T> {
//    if (this is UiState.Success) {
//        action(data)
//    }
//    return this
//}
//
///**
// * Execute action only if state is error
// */
//inline fun UiState<*>.onError(action: (AppException) -> Unit): UiState<*> {
//    if (this is UiState.Error) {
//        action(exception)
//    }
//    return this
//}
//
///**
// * Execute action only if state is loading
// */
//inline fun UiState<*>.onLoading(action: (String?) -> Unit): UiState<*> {
//    if (this is UiState.Loading) {
//        action(message)
//    }
//    return this
//}