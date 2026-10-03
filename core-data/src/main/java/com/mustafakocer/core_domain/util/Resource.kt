package com.mustafakocer.core_domain.util

import com.mustafakocer.core_domain.exception.AppException

/**
 * A sealed wrapper class that represents the state of a data request.
 *
 * Architectural Note:
 * This class is fundamental for communicating the status of asynchronous operations (like API
 * calls) from the data/domain layers to the UI layer. It explicitly handles loading, success,

 * and error states, preventing nullability issues and making state management in ViewModels
 * and Composables much cleaner and more predictable.
 *
 * @param T The type of data held in the case of success.
 */
sealed class Resource<out T> {
    /** Represents the state where the data is currently being loaded. */
    data object Loading : Resource<Nothing>()

    /** Represents the state where the data was successfully fetched. */
    data class Success<out T>(val data: T) : Resource<T>()

    /** Represents the state where an error occurred during the data fetch. */
    data class Error(val exception: AppException) : Resource<Nothing>()
}