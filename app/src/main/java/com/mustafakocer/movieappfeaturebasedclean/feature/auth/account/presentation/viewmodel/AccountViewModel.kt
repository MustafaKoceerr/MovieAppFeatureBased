package com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.domain.usecase.LogoutUseCase
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.domain.usecase.ObserveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountUiState(
    val isLoggedIn: Boolean = false,
    /** True after a completed logout; the route navigates to the welcome screen. */
    val loggedOut: Boolean = false,
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    private val sessionObserverJob: Job = observeSessionUseCase()
        .onEach { sessionId -> _uiState.update { it.copy(isLoggedIn = !sessionId.isNullOrBlank()) } }
        .launchIn(viewModelScope)

    fun onLogoutClick() {
        // Stop observing so the UI does not flash the guest state before navigating away.
        sessionObserverJob.cancel()
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { it.copy(loggedOut = true) }
        }
    }
}
