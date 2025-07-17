package com.mustafakocer.core_common.presentation

import com.mustafakocer.core_common.exception.AppException

interface BaseUiState {
    val isLoading: Boolean
    val isRefreshing: Boolean
    val error: AppException?
}

/**
 * Marker interface for UI events
 */
interface BaseUiEvent

/**
 * Marker interface for UI effects
 */
interface BaseUiEffect