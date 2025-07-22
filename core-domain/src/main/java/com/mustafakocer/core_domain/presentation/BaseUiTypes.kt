package com.mustafakocer.core_domain.presentation

import com.mustafakocer.core_domain.exception.AppException

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