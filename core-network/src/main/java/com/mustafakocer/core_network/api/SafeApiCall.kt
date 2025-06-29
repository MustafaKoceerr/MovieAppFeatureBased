package com.mustafakocer.core_network.api

import com.mustafakocer.core_common.UiState
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_network.error.ErrorMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

// # Modern safe API call
/**
 * TEACHING MOMENT: Modern SafeApiCall Implementation
 *
 * IMPROVEMENTS OVER OLD VERSION:
 * ✅ UiState instead of Resource
 * ✅ AppException instead of String errors
 * ✅ Centralized error mapping
 * ✅ Better type safety
 * ✅ Consistent error categorization
 */

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