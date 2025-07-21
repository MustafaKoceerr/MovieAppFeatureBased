package com.mustafakocer.feature_auth.welcome.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.feature_auth.shared.util.AuthConstants
import com.mustafakocer.feature_auth.welcome.domain.provider.AuthCallbackProvider
import com.mustafakocer.feature_auth.welcome.domain.usecase.CreateRequestTokenUseCase
import com.mustafakocer.feature_auth.welcome.domain.usecase.CreateSessionUseCase
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeEffect
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeEvent
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val createRequestTokenUseCase: CreateRequestTokenUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val authCallbackProvider: AuthCallbackProvider,
) : BaseViewModel<WelcomeUiState, WelcomeEvent, WelcomeEffect>(
    initialState = WelcomeUiState()
) {

    init {
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
        handleResource(
            resourceFlow = createRequestTokenUseCase(),
            onSuccess = { requestToken ->
                val tmdbAuthUrl = "${AuthConstants.TMDB_AUTHENTICATION_URL}$requestToken?redirect_to=${AuthConstants.REDIRECT_URL}"
                sendEffect(WelcomeEffect.NavigateToTmdbLogin(url = tmdbAuthUrl))
            }
        )
    }

    private fun listenForAuthCallback() {
        authCallbackProvider.tokenFlow.onEach { approvedToken ->
            // Onaylanmış token geldi, şimdi bu token ile session oluştur.
            handleResource(
                resourceFlow = createSessionUseCase(approvedToken),
                onSuccess = {
                    // Başarılı! Home ekranına yönlendir.
                    sendEffect(WelcomeEffect.NavigateToHome)
                }
            )
        }.launchIn(viewModelScope)
    }

    /**
     * Resource akışlarını işlemek için merkezi bir yardımcı fonksiyon.
     * Bu, 'when' bloğunu ve state güncellemelerini tek bir yerde toplayarak kod tekrarını önler.
     *
     * @param T Resource içindeki başarılı veri tipi.
     * @param resourceFlow İşlenecek olan Flow<Resource<T>>.
     * @param onSuccess Kaynak başarılı olduğunda çalıştırılacak olan eylem.
     */
    private fun <T> handleResource(
        resourceFlow: Flow<Resource<T>>,
        onSuccess: (data: T) -> Unit
    ) {
        resourceFlow.onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    setState { copy(isLoading = true, error = null) }
                }
                is Resource.Success<*> -> {
                    setState { copy(isLoading = false) }
                    // Smart cast'in çalışması için veriyi güvenli bir şekilde alıyoruz.
                    (resource.data as? T)?.let(onSuccess)
                }
                is Resource.Error -> {
                    setState { copy(isLoading = false, error = resource.exception) }
                }
            }
        }.launchIn(viewModelScope)
    }
}