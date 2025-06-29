package com.mustafakocer.core_common.presentation

/**
 * TEACHING MOMENT: Base UI Type Definitions
 *
 * Group related base types together - they're all simple marker interfaces
 */
interface BaseUiState {
    val isLoading: Boolean
    val error: String?
}

/**
 * Marker interface for UI events
 */
interface BaseUiEvent

/**
 * Marker interface for UI effects
 */
interface BaseUiEffect