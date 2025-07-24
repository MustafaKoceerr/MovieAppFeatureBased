package com.mustafakocer.feature_splash.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.feature_splash.presentation.contract.SplashEffect
import com.mustafakocer.feature_splash.presentation.contract.SplashEvent
import com.mustafakocer.feature_splash.presentation.contract.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Manages the business logic for the Splash screen.
 *
 * @param sessionProvider The provider for checking the current user session state.
 *
 * Architectural Note:
 * The primary responsibility of this ViewModel is to determine the user's authentication status
 * and then emit a navigation effect. It orchestrates two parallel tasks: a minimum display timer
 * and a session check. This ensures the splash screen is displayed for a pleasant duration while
 * efficiently deciding the next navigation destination.
 */
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
            // Architectural Decision:
            // Two jobs are launched in parallel using `async` for efficiency.
            // - `timerJob`: Guarantees a minimum branding display time, improving user experience.
            // - `sessionJob`: Fetches the session state from the data layer.
            // `awaitAll` ensures that we wait for the slower of the two tasks to complete before
            // proceeding, which is the most efficient way to handle this dual requirement.
            val timerJob = async { delay(1500L) }
            val sessionJob = async {
                runCatching { sessionProvider.observeSessionId().first() }
            }

            awaitAll(timerJob, sessionJob)

            val sessionResult = sessionJob.await()
            sessionResult.onSuccess { sessionId ->
                if (sessionId.isNullOrBlank()) {
                    sendEffect(SplashEffect.NavigateToWelcome)
                } else {
                    sendEffect(SplashEffect.NavigateToHome)
                }
            }.onFailure {
                // In case of an error reading the session, default to the welcome screen.
                sendEffect(SplashEffect.NavigateToWelcome)
            }
        }
    }

    override fun onEvent(event: SplashEvent) {
        // No user interactions on this screen.
    }
}