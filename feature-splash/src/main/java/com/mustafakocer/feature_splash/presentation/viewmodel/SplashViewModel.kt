package com.mustafakocer.feature_splash.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_common.provider.SessionProvider
import com.mustafakocer.feature_splash.presentation.contract.SplashEffect
import com.mustafakocer.feature_splash.presentation.contract.SplashEvent
import com.mustafakocer.feature_splash.presentation.contract.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionProvider: SessionProvider,
) : BaseViewModel<SplashUiState, SplashEvent, SplashEffect>(
    initialState = SplashUiState()
) {

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            // Splash ekranının çok hızlı geçmemesi için küçük bir gecikme ekleyebiliriz.
            // Bu, kullanıcıya uygulamanın yüklendiği hissini verir.
            delay(1000L)

            val sessionId = sessionProvider.observeSessionId().first()
            if (sessionId.isNullOrBlank()) {
                // session yok, Welcome ekranına git.
                sendEffect(SplashEffect.NavigateToWelcome)
            } else {
                // Session var, Home ekranına git.
                sendEffect(SplashEffect.NavigateToHome)
            }
        }
    }

    // Kullanıcı etkileşimi olmadığı için boş
    override fun onEvent(event: SplashEvent) {
    }
}