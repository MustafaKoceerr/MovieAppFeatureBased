package com.mustafakocer.feature_auth.presentation.welcome.viewmodel

import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeEffect
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeEvent
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(): BaseViewModel<WelcomeUiState, WelcomeEvent, WelcomeEffect>(
    initialState = WelcomeUiState()
) {
    override fun onEvent(event: WelcomeEvent) {
        when(event){
            is WelcomeEvent.GuestClicked -> {
                // Misafir olarak devam et'e tıklandığında, Home ekranına git.
                sendEffect(WelcomeEffect.NavigateToHome)
            }
            is WelcomeEvent.LoginClicked -> {
                // TODO: Adım 2'de burayı dolduracağız.
                // 1. RequestTokenUseCase'i çağır.
                // 2. Dönen URL ile NavigateToTmdbLogin effect'i gönder.
            }        }
    }

}