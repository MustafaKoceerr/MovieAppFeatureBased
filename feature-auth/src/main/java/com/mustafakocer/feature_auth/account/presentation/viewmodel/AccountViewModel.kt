package com.mustafakocer.feature_auth.account.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.feature_auth.account.domain.usecase.LogoutUseCase
import com.mustafakocer.feature_auth.account.domain.usecase.ObserveSessionUseCase
import com.mustafakocer.feature_auth.account.presentation.contract.AccountEffect
import com.mustafakocer.feature_auth.account.presentation.contract.AccountEvent
import com.mustafakocer.feature_auth.account.presentation.contract.AccountUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Manages the business logic for the Account screen.
 *
 * @param observeSessionUseCase For reactively observing the user's authentication status.
 * @param logoutUseCase For handling the user logout process.
 *
 * Architectural Note:
 * This ViewModel's primary role is to reflect the user's session state in the UI. It uses
 * `ObserveSessionUseCase` to create a long-lived, reactive subscription to the auth state.
 * This ensures the UI is always in sync without needing to poll for changes.
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel<AccountUiState, AccountEvent, AccountEffect>(
    initialState = AccountUiState()
) {
    /** Holds the coroutine job that is actively observing the session state, allowing it to be cancelled. */
    private var sessionObserverJob: Job? = null

    init {
        observeSessionState()
    }

    private fun observeSessionState() {
        // The existing job is cancelled before starting a new one to prevent multiple collectors.
        sessionObserverJob?.cancel()
        sessionObserverJob = observeSessionUseCase().onEach { sessionId ->
            setState { copy(isLoggedIn = !sessionId.isNullOrBlank()) }
        }.launchIn(viewModelScope)
    }

    override fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.LogoutClicked -> handleLogout()
            is AccountEvent.LoginClicked -> sendEffect(AccountEffect.NavigateToWelcome)
            is AccountEvent.BackClicked -> sendEffect(AccountEffect.NavigateBack)
        }
    }

    private fun handleLogout() {
        // Architectural Decision:
        // The state observer is cancelled *before* initiating the logout. This prevents the
        // observer from reacting to the session change while the logout is still in progress,
        // ensuring a clean and predictable state transition before navigation.
        sessionObserverJob?.cancel()

        viewModelScope.launch {
            logoutUseCase()
            sendEffect(AccountEffect.NavigateToWelcome)
        }
    }
}