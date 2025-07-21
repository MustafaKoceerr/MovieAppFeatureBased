package com.mustafakocer.feature_auth.account.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.feature_auth.account.domain.usecase.LogoutUseCase
import com.mustafakocer.feature_auth.account.domain.usecase.ObserveSessionUseCase
import com.mustafakocer.feature_auth.account.presentation.contract.AccountEffect
import com.mustafakocer.feature_auth.account.presentation.contract.AccountEvent
import com.mustafakocer.feature_auth.account.presentation.contract.AccountUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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

    /**
     * Oturum durumundaki değişiklikleri reaktif olarak dinler ve UI state'ini günceller.
     */
    private fun observeSessionState() {
        observeSessionUseCase().onEach { sessionId ->
            // Session ID'nin varlığına (null veya boş olmamasına) göre isLoggedIn durumunu ayarla.
            setState { copy(isLoggedIn = !sessionId.isNullOrBlank()) }
        }.launchIn(viewModelScope)
    }

    override fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.LogoutClicked -> {
                // Logout işlemi bir suspend fonksiyon olduğu için coroutine içinde çağrılmalı.
                viewModelScope.launch {
                    logoutUseCase()
                    // Çıkış yapma işlemi bittikten sonra, kullanıcıyı Welcome ekranına yönlendir.
                    // Navigasyon grafiğimiz bu geçiş sırasında yığını temizleyecektir.
                    sendEffect(AccountEffect.NavigateToWelcome)
                }
            }

            is AccountEvent.LoginClicked -> {
                // Kullanıcı misafir ise ve giriş yapmak isterse, onu da Welcome ekranına yönlendir.
                sendEffect(AccountEffect.NavigateToWelcome)
            }

            is AccountEvent.BackClicked -> {
                // Geri tuşuna basıldığında bir önceki ekrana dön.
                sendEffect(AccountEffect.NavigateBack)
            }
        }
    }
}