package com.mustafakocer.core_common.presentation

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface UiContract<State, Event, Effect> {
    /**
     * UI State stream - Single source of truth for UI
     * @return StateFlow that emits current UI state
     */
    val uiState: StateFlow<State>

    /**
     * UI Effect stream - One-time side effects
     * @return SharedFlow that emits UI effects (navigation, toasts, etc.)
     */
    val uiEffect: SharedFlow<Effect>

    /**
     * Event handler - User intents from UI
     * @param event User action/intent to be processed
     */
    fun onEvent(event: Event)
}

