package com.mustafakocer.feature_auth.presentation.welcome.contract

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
}

sealed interface WelcomeEffect : BaseUiEffect {
    object NavigateToHome : WelcomeEffect
    // Gelecekte buraya web tarayıcısını açmak için bir effect ekleyeceğiz.
    // data class NavigateToTmdbLogin(val url: String) : WelcomeEffect
}