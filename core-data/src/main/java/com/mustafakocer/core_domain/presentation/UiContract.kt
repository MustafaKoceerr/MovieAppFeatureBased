package com.mustafakocer.core_domain.presentation

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Defines a standardized MVI (Model-View-Intent) contract for communication between a UI
 * component (View) and its corresponding ViewModel.
 *
 * @param State The type of the UI state, typically a data class implementing [BaseUiState].
 * @param Event The type of the UI events, typically a sealed class implementing [BaseUiEvent].
 * @param Effect The type of the side effects, typically a sealed class implementing [BaseUiEffect].
 *
 * Architectural Note:
 * This generic interface is the cornerstone of the MVI architecture in this project. It enforces
 * a unidirectional data flow and a clear separation of concerns for every feature:
 * - The UI observes `uiState` to render itself.
 * - The UI sends user actions via `onEvent`.
 * - The UI observes `uiEffect` for one-time actions like navigation or toasts.
 * This consistency simplifies development, testing, and debugging across the entire application.
 */
interface UiContract<State, Event, Effect> {
    /**
     * The stream of UI state, representing the single source of truth for the screen.
     */
    val uiState: StateFlow<State>

    /**
     * The stream for one-time side effects that should not be re-consumed on configuration change.
     */
    val uiEffect: SharedFlow<Effect>

    /**
     * The single entry point for the UI to send events (user intents) to the ViewModel.
     */
    fun onEvent(event: Event)
}