package com.mustafakocer.feature_splash.presentation.contract

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.presentation.BaseUiEffect
import com.mustafakocer.core_domain.presentation.BaseUiEvent
import com.mustafakocer.core_domain.presentation.BaseUiState

class SplashUiState : BaseUiState {
    override val isLoading: Boolean = true // The screen is always in a loading state.
    override val isRefreshing: Boolean = false
    override val error: AppException? = null
}


sealed interface SplashEvent : BaseUiEvent


sealed interface SplashEffect : BaseUiEffect {
    object NavigateToHome : SplashEffect

    object NavigateToWelcome : SplashEffect
}