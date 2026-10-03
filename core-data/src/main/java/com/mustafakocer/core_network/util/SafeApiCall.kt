package com.mustafakocer.core_network.util

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_network.error.ErrorMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

/**
 * A centralized wrapper for executing Retrofit API calls safely and converting them into a [Flow] of [Resource].
 *
 * Architectural Note:
 * This function is a cornerstone of the data layer. It standardizes the entire lifecycle of a network request:
 * 1.  **State Management:** It emits [Resource.Loading] immediately, providing a consistent signal for UIs.
 * 2.  **Error Handling:** It uses a central [ErrorMapper] to translate all possible network errors (both HTTP codes and exceptions) into a predictable [AppException].
 * 3.  **Thread Safety:** It guarantees that all network operations are performed on the `Dispatchers.IO` thread.
 * By abstracting this logic, repositories can focus solely on invoking the API call, leading to cleaner, more maintainable, and less error-prone code.
 *
 * @param T The DTO (Data Transfer Object) type expected from the API response.
 * @param apiCall A suspend lambda function that performs the actual Retrofit network request.
 * @return A [Flow] that emits the state of the API request, encapsulated within a [Resource].
 */
fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>,
): Flow<Resource<T>> = flow {
    // Immediately emit the loading state to notify collectors that the operation has started.
    emit(Resource.Loading)

    val response = apiCall()

    if (response.isSuccessful) {
        val body = response.body()
        if (body != null) {
            // If the call is successful and the body is not null, emit the data.
            emit(Resource.Success(body))
        } else {
            // A 2xx response with a null body is considered a data error in this architecture.
            emit(Resource.Error(AppException.Data.EmptyResponse))
        }
    } else {
        // Handle non-2xx HTTP error responses by mapping them to a domain-specific exception.
        emit(Resource.Error(ErrorMapper.mapHttpErrorResponseToAppException(response)))
    }
}.catch { throwable ->
    // Catch any exceptions thrown during the API call (e.g., IOException, HttpException).
    // Map the throwable to a domain-specific exception.
    emit(Resource.Error(ErrorMapper.mapThrowableToAppException(throwable)))
}.flowOn(Dispatchers.IO) // Ensure all operations within this flow execute on the IO thread pool.