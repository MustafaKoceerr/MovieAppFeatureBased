package com.mustafakocer.feature_auth.presentation.welcome.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.feature_auth.domain.provider.AuthCallbackProvider
import com.mustafakocer.feature_auth.domain.usecase.CreateRequestTokenUseCase
import com.mustafakocer.feature_auth.domain.usecase.CreateSessionUseCase
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeEffect
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeEvent
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeUiState
import com.mustafakocer.feature_auth.util.AuthConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val createRequestTokenUseCase: CreateRequestTokenUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val authCallbackProvider: AuthCallbackProvider,
) :
    BaseViewModel<WelcomeUiState, WelcomeEvent, WelcomeEffect>(
        initialState = WelcomeUiState()
    ) {

    init {
        listenForAuthCallback()
    }

    override fun onEvent(event: WelcomeEvent) {
        when (event) {
            is WelcomeEvent.LoginClicked -> {
                handleLoginClick()
            }

            is WelcomeEvent.GuestClicked -> {
                sendEffect(WelcomeEffect.NavigateToHome)
            }

            is WelcomeEvent.DismissError -> {
                setState { copy(error = null) }
            }
        }
    }

    private fun handleLoginClick() {
        createRequestTokenUseCase().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    setState { copy(isLoading = true, error = null) }
                }

                is Resource.Success -> {
                    setState { copy(isLoading = false) }
                    val requestToken = resource.data
                    // Hard-coded string yerine sabiti kullanıyoruz.
                    val tmdbAuthUrl =
                        "${AuthConstants.TMDB_AUTHENTICATION_URL}$requestToken?redirect_to=${AuthConstants.REDIRECT_URL}"

                    sendEffect(WelcomeEffect.NavigateToTmdbLogin(url = tmdbAuthUrl))
                }

                is Resource.Error -> {
                    setState { copy(isLoading = false, error = resource.exception) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun listenForAuthCallback() {
        authCallbackProvider.tokenFlow.onEach { approvedToken ->
            // Onaylanmış token geldi, şimdi bu token ile session oluştur.
            createSession(approvedToken)
        }.launchIn(viewModelScope)
    }

    private fun createSession(approvedToken: String) {
        // createSessionUseCase'den dönen Flow'u dinle ve sonucu işle.
        createSessionUseCase(approvedToken).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Kullanıcı uygulamaya döndüğünde bir yükleme göstergesi göster.
                    setState { copy(isLoading = true, error = null) }
                }

                is Resource.Success<*> -> {
                    // Başarılı! DataStore'a kaydetme işlemi Repository'de yapıldı.
                    // Şimdi kullanıcıyı Home ekranına yönlendir.
                    setState { copy(isLoading = false) }
                    sendEffect(WelcomeEffect.NavigateToHome)
                }

                is Resource.Error -> {
                    // Session oluşturulurken bir hata oldu.
                    setState { copy(isLoading = false, error = resource.exception) }
                }
            }
        }.launchIn(viewModelScope)
    }
}
