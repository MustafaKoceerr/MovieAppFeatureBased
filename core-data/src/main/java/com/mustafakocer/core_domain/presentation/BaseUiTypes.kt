package com.mustafakocer.core_domain.presentation

import com.mustafakocer.core_domain.exception.AppException

/**
 * Defines the foundational contract for any screen's UI state.
 *
 * Architectural Note:
 * By enforcing this base interface, we guarantee that every feature's UI state will consistently
 * handle common concerns like loading indicators and error displays. This promotes a predictable
 * user experience and reduces boilerplate code in individual feature ViewModels.
 */
interface BaseUiState {
    val isLoading: Boolean
    val isRefreshing: Boolean
    val error: AppException?
}

/**
 * A marker interface for all UI events.
 */
interface BaseUiEvent

/**
 * A marker interface for all single-fire UI effects.
 */
interface BaseUiEffect