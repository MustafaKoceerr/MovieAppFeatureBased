package com.mustafakocer.feature_splash.presentation.contract

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.presentation.BaseUiEffect
import com.mustafakocer.core_domain.presentation.BaseUiEvent
import com.mustafakocer.core_domain.presentation.BaseUiState

// Splash ekranının göstereceği bir UI durumu olmadığı için bu boş kalabilir.
// Sadece BaseUiState'i implemente etmesi yeterli.
class SplashUiState : BaseUiState {
    override val isLoading: Boolean = true // Her zaman yükleniyor durumunda
    override val isRefreshing: Boolean = false
    override val error: AppException? = null
}

// Splash ekranında kullanıcı etkileşimi olmadığı için bu boş.
sealed interface SplashEvent : BaseUiEvent

sealed interface SplashEffect : BaseUiEffect {
    object NavigateToHome : SplashEffect
    object NavigateToWelcome : SplashEffect
}