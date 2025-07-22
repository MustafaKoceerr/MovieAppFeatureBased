package com.mustafakocer.feature_splash.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.feature_splash.presentation.contract.SplashEffect
import com.mustafakocer.feature_splash.presentation.contract.SplashEvent
import com.mustafakocer.feature_splash.presentation.contract.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
            // 1. İki işlemi paralel olarak başlatıyoruz.
            //    - 'timerJob' en az 1 saniye beklemeyi garanti eder.
            //    - 'sessionJob' oturum bilgisini alır.
            val timerJob = async { delay(1000L) } // 1 saniyelik minimum gecikme
            val sessionJob = async {
                runCatching { sessionProvider.observeSessionId().first() }
            }

            // 2. İki işlemin de tamamlanmasını bekliyoruz.
            //    - Eğer sessionJob 200ms'de biterse, bu satır kalan 800ms'yi bekler.
            //    - Eğer sessionJob 1200ms'de biterse, timerJob zaten bitmiş olacağı için
            //      bu satır sadece sessionJob'un kalan 200ms'sini bekler.
            awaitAll(timerJob, sessionJob)

            // 3. Artık her iki iş de bittiğine göre, oturum kontrolünün sonucunu işleyebiliriz.
            val sessionResult = sessionJob.await() // Sonucu al
            sessionResult.onSuccess { sessionId ->
                if (sessionId.isNullOrBlank()) {
                    sendEffect(SplashEffect.NavigateToWelcome)
                } else {
                    sendEffect(SplashEffect.NavigateToHome)
                }
            }.onFailure { exception ->
                sendEffect(SplashEffect.NavigateToWelcome) // Hata durumunda varsayılan ekrana git
            }
        }
    }

    // Kullanıcı etkileşimi olmadığı için boş
    override fun onEvent(event: SplashEvent) {
    }
}