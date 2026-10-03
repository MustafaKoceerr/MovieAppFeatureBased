package com.mustafakocer.movieappfeaturebasedclean.feature.splash.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_domain.provider.SessionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class SplashDestination { Home, Welcome }

/** [destination] is null while the splash is showing; once set, the route navigates away. */
data class SplashUiState(val destination: SplashDestination? = null)

/**
 * Waits for the minimum splash time and the stored session in parallel,
 * then decides where the user should land.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionProvider: SessionProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            val timerJob = async { delay(MIN_SPLASH_MILLIS) }
            val sessionJob = async {
                runCatching { sessionProvider.observeSessionId().first() }
            }
            awaitAll(timerJob, sessionJob)

            val hasSession = sessionJob.await().getOrNull()?.isNotBlank() == true
            _uiState.value = SplashUiState(
                destination = if (hasSession) SplashDestination.Home else SplashDestination.Welcome
            )
        }
    }

    private companion object {
        const val MIN_SPLASH_MILLIS = 1500L
    }
}
