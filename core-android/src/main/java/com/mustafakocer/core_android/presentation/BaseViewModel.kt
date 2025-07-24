package com.mustafakocer.core_android.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_domain.presentation.BaseUiEffect
import com.mustafakocer.core_domain.presentation.BaseUiEvent
import com.mustafakocer.core_domain.presentation.BaseUiState
import com.mustafakocer.core_domain.presentation.UiContract
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * An abstract ViewModel that implements the [UiContract] to enforce a consistent MVI pattern.
 * It manages the state and effect streams, providing a structured foundation for all ViewModels
 * in the application.
 *
 * @param State The type of the UI state, constrained to [BaseUiState].
 * @param Event The type of the UI events, constrained to [BaseUiEvent].
 * @param Effect The type of the side effects, constrained to [BaseUiEffect].
 * @param initialState The starting state for the UI.
 */
abstract class BaseViewModel<
        State : BaseUiState,
        Event : BaseUiEvent,
        Effect : BaseUiEffect,
        >(
    initialState: State,
) : ViewModel(), UiContract<State, Event, Effect> {

    private val _uiState = MutableStateFlow(initialState)
    override val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<Effect>(
        replay = 0,
        extraBufferCapacity = 1
    )
    override val uiEffect: SharedFlow<Effect> = _uiEffect.asSharedFlow()

    /**
     * Handles incoming UI events. This must be implemented by concrete ViewModel classes.
     */
    abstract override fun onEvent(event: Event)

    /**
     * Provides read-only access to the current UI state.
     */
    protected val currentState: State
        get() = _uiState.value

    /**
     * Updates the UI state immutably by applying a reduction function to the current state.
     *
     * @param reduce A lambda function that receives the current state and returns a new state.
     */
    protected fun setState(reduce: State.() -> State) {
        _uiState.value = currentState.reduce()
    }

    /**
     * Sends a one-time side effect to be consumed by the UI.
     *
     * @param effect The effect to be sent.
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _uiEffect.emit(effect)
        }
    }
}

/**
 * Defines the type of loading indicator to be displayed in the UI.
 */
enum class LoadingType {
    /** For initial data loading, typically represented by a full-screen or large indicator. */
    MAIN,

    /** For subsequent data refreshes, like a pull-to-refresh action. */
    REFRESH
}