package com.mustafakocer.feature_auth.welcome.presentation.contract

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.core_common.presentation.BaseUiState

data class WelcomeUiState(
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState

sealed interface WelcomeEvent : BaseUiEvent {
    object LoginClicked : WelcomeEvent
    object GuestClicked : WelcomeEvent
    object DismissError : WelcomeEvent // Hata mesajını kapatmak için
}

sealed interface WelcomeEffect : BaseUiEffect {
    object NavigateToHome : WelcomeEffect
    data class NavigateToTmdbLogin(val url: String) : WelcomeEffect
}