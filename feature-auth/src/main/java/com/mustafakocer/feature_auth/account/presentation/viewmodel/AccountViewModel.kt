package com.mustafakocer.feature_auth.account.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.feature_auth.account.domain.usecase.LogoutUseCase
import com.mustafakocer.feature_auth.account.domain.usecase.ObserveSessionUseCase
import com.mustafakocer.feature_auth.account.presentation.contract.AccountEffect
import com.mustafakocer.feature_auth.account.presentation.contract.AccountEvent
import com.mustafakocer.feature_auth.account.presentation.contract.AccountUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val observeSessionUseCase: ObserveSessionUseCase,
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel<AccountUiState, AccountEvent, AccountEffect>(
    initialState = AccountUiState()
) {
    init {
        // ViewModel oluşturulduğunda, kullanıcının oturum durumunu dinlemeye başla.
        observeSessionState()
    }
    // Oturum durumunu dinleyen coroutine işini (Job) tutmak için bir değişken.
    private var sessionObserverJob: Job? = null
    /**
     * Oturum durumundaki değişiklikleri reaktif olarak dinler ve UI state'ini günceller.
     */
    private fun observeSessionState() {
        // Eğer zaten bir dinleyici çalışıyorsa, yenisini başlatmadan önce eskisini iptal et.
        sessionObserverJob?.cancel()
        sessionObserverJob = observeSessionUseCase().onEach { sessionId ->
            setState { copy(isLoggedIn = !sessionId.isNullOrBlank()) }
        }.launchIn(viewModelScope)
    }

    override fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.LogoutClicked -> {
                handleLogout()
            }
            is AccountEvent.LoginClicked -> {
                sendEffect(AccountEffect.NavigateToWelcome)
            }
            is AccountEvent.BackClicked -> {
                sendEffect(AccountEffect.NavigateBack)
            }
        }
    }

    private fun handleLogout() {
        // 1. Önce, state'i güncelleyebilecek olan dinleyiciyi durdur.
        sessionObserverJob?.cancel()

        // 2. Şimdi, güvenli bir şekilde çıkış işlemini yap.
        viewModelScope.launch {
            logoutUseCase()
            // 3. Çıkış işlemi bittikten sonra navigasyonu tetikle.
            sendEffect(AccountEffect.NavigateToWelcome)
        }
    }
}