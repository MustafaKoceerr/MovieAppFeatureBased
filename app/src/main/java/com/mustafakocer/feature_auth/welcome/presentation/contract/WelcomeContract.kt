package com.mustafakocer.feature_auth.welcome.presentation.contract

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.presentation.BaseUiEffect
import com.mustafakocer.core_domain.presentation.BaseUiEvent
import com.mustafakocer.core_domain.presentation.BaseUiState

data class WelcomeUiState(
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState


sealed interface WelcomeEvent : BaseUiEvent {
    object LoginClicked : WelcomeEvent

    object GuestClicked : WelcomeEvent

    object DismissError : WelcomeEvent
}


sealed interface WelcomeEffect : BaseUiEffect {
    object NavigateToHome : WelcomeEffect

    /**
     * Signals a navigation event to an external browser for TMDB authentication.
     * @param url The fully-formed URL for the TMDB authentication page.
     */
    data class NavigateToTmdbLogin(val url: String) : WelcomeEffect
}