package com.mustafakocer.core_domain.util

/**
 * Maps a `Resource<T>` to a `Resource<R>` by applying a transformation to the `Success`
 * data, while preserving the `Error` and `Loading` states.
 *
 * Architectural Note:
 * This is a key utility for the data layer. It simplifies the process of converting data
 * transfer objects (DTOs) from the network/database into domain models. By using this
 * extension, repositories can transform the success data without writing repetitive `when`
 * blocks, ensuring that loading and error states are always propagated correctly.
 *
 * @param T The original data type.
 * @param R The target data type.
 * @param transform The function to apply to the data if the resource is `Success`.
 * @return A new `Resource<R>` instance containing the transformed data or the original state.
 */
fun <T, R> Resource<T>.mapSuccess(transform: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Success -> Resource.Success(transform(data))
        is Resource.Error -> this
        is Resource.Loading -> this
    }
}