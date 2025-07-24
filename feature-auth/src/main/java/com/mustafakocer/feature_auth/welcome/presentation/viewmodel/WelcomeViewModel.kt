package com.mustafakocer.feature_auth.welcome.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.feature_auth.shared.util.AuthConstants
import com.mustafakocer.feature_auth.welcome.domain.handler.AuthCallbackHandler
import com.mustafakocer.feature_auth.welcome.domain.usecase.CreateRequestTokenUseCase
import com.mustafakocer.feature_auth.welcome.domain.usecase.CreateSessionUseCase
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeEffect
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeEvent
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Manages the user authentication flow for the Welcome screen.
 *
 * @param createRequestTokenUseCase For initiating the login process by getting a request token.
 * @param createSessionUseCase For finalizing the login with an approved token.
 * @param authCallbackHandler A bridge to receive the callback token from the web auth flow.
 *
 * Architectural Note:
 * This ViewModel orchestrates the entire multi-step TMDB login process. Its key design features are:
 * 1.  **Decoupling with a Handler:** It uses `AuthCallbackHandler` to receive the result from the
 *     web authentication flow. This decouples the ViewModel from the Android component (e.g., an
 *     Activity) that captures the deep link, preventing lifecycle issues and direct dependencies.
 * 2.  **Centralized Resource Handling:** A private extension function, `handleResource`, is used
 *     to process `Resource` streams. This avoids repetitive `when` blocks and centralizes the
 *     logic for updating loading and error states, leading to cleaner and more maintainable code.
 */
@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val createRequestTokenUseCase: CreateRequestTokenUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val authCallbackHandler: AuthCallbackHandler,
) : BaseViewModel<WelcomeUiState, WelcomeEvent, WelcomeEffect>(
    initialState = WelcomeUiState()
) {

    init {
        // Immediately start listening for an approved token from the web auth callback.
        listenForAuthCallback()
    }

    override fun onEvent(event: WelcomeEvent) {
        when (event) {
            is WelcomeEvent.LoginClicked -> handleLoginClick()
            is WelcomeEvent.GuestClicked -> sendEffect(WelcomeEffect.NavigateToHome)
            is WelcomeEvent.DismissError -> setState { copy(error = null) }
        }
    }

    private fun handleLoginClick() {
        createRequestTokenUseCase()
            .handleResource(
                onSuccess = { requestToken ->
                    val tmdbAuthUrl = "${AuthConstants.TMDB_AUTHENTICATION_URL}$requestToken?redirect_to=${AuthConstants.REDIRECT_URL}"
                    sendEffect(WelcomeEffect.NavigateToTmdbLogin(url = tmdbAuthUrl))
                }
            )
    }

    private fun listenForAuthCallback() {
        authCallbackHandler.tokenFlow
            .onEach { approvedToken ->
                // An approved token has been received; now create a session with it.
                createSessionUseCase(approvedToken)
                    .handleResource(
                        onSuccess = {
                            // Session created successfully, navigate to the main app content.
                            sendEffect(WelcomeEffect.NavigateToHome)
                        }
                    )
            }
            .launchIn(viewModelScope)
    }

    /**
     * A private extension function to centralize the handling of `Resource` streams.
     * This reduces boilerplate by managing the `when` block for `Loading`, `Success`,
     * and `Error` states and standardizing UI state updates.
     *
     * @param T The type of data within the `Resource.Success` state.
     * @param onSuccess A lambda to be executed only when the resource is `Success`.
     */
    private fun <T> Flow<Resource<T>>.handleResource(
        onSuccess: (data: T) -> Unit
    ) {
        this.onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    setState { copy(isLoading = true, error = null) }
                }
                is Resource.Success -> {
                    setState { copy(isLoading = false) }
                    onSuccess(resource.data)
                }
                is Resource.Error -> {
                    setState { copy(isLoading = false, error = resource.exception) }
                }
            }
        }.launchIn(viewModelScope)
    }
}