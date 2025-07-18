package com.mustafakocer.core_android.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.exception.toAppException
import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.core_common.presentation.UiContract
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Base class for all ViewModels that follow the MVI pattern with UiContract.
 *
 * @param State The type of the UI state. Must implement BaseUiState.
 * @param Event The type of the UI events.
 * @param Effect The type of the UI side effects.
 * @param initialState The initial state of the UI.
 */
abstract class BaseViewModel<
        State : BaseUiState,
        Event : BaseUiEvent,
        Effect : BaseUiEffect,
        >(
    initialState: State,
) : ViewModel(), UiContract<State, Event, Effect> {

    // PRIVATE STATE STREAM
    private val _uiState = MutableStateFlow(initialState)
    override val uiState: StateFlow<State> = _uiState.asStateFlow()

    // PRIVATE EFFECT STREAM (SharedFlow Kullanımı)
    // Channel yerine, bu senaryo için tasarlanmış olan MutableSharedFlow kullanıyoruz.
    private val _uiEffect = MutableSharedFlow<Effect>(
        /**
         * replay = 0: Yeni bir dinleyici (örn. ekran döndükten sonra UI) bağlandığında,
         * geçmişteki effect'leri tekrar göndermez. Bu, Toast'ların tekrar tekrar
         * gösterilmesini engeller. Hayati öneme sahiptir.
         */
        replay = 0,
        /**
         * extraBufferCapacity = 1: Bir effect gönderildiğinde, dinleyici hazır olmasa bile
         * coroutine'in askıda kalmasını (suspend) önler. Akıcılığı artırır.
         */
        extraBufferCapacity = 1
    )
    override val uiEffect: SharedFlow<Effect> = _uiEffect.asSharedFlow()

    // EVENT HANDLER (Must be implemented by subclasses)
    abstract override fun onEvent(event: Event)

    /**
     * Gets the current UI state.
     */
    protected val currentState: State
        get() = _uiState.value

    /**
     * Updates the UI state using a reducer function.
     *
     * @param reduce A function that takes the current state and returns the new state.
     */
    protected fun setState(reduce: State.() -> State) {
        _uiState.value = currentState.reduce()
    }

    /**
     * Sends a one-time side effect to the UI.
     *
     * @param effect The side effect to be sent.
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            // Channel'daki 'send' yerine, SharedFlow için 'emit' kullanılır.
            _uiEffect.emit(effect)
        }
    }
}

/**
 * Defines the type of loading indicator to show.
 */
enum class LoadingType {
    MAIN, // For initial screen load, usually a full-screen loader
    REFRESH // For pull-to-refresh, usually a swipe indicator
}