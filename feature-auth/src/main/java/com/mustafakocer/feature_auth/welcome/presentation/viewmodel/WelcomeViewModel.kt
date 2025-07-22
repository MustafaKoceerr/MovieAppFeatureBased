// GÜNCELLENMİŞ VE EN İDEAL DOSYA

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val createRequestTokenUseCase: CreateRequestTokenUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val authCallbackHandler: AuthCallbackHandler, // <-- Artık somut sınıfı enjekte ediyoruz
) : BaseViewModel<WelcomeUiState, WelcomeEvent, WelcomeEffect>(
    initialState = WelcomeUiState()
) {

    init {
        // Tarayıcıdan dönebilecek onaylanmış token'ı dinlemeye başla.
        listenForAuthCallback()
    }

    override fun onEvent(event: WelcomeEvent) {
        when (event) {
            is WelcomeEvent.LoginClicked -> handleLoginClick()
            is WelcomeEvent.GuestClicked -> sendEffect(WelcomeEffect.NavigateToHome)
            is WelcomeEvent.DismissError -> setState { copy(error = null) }
        }
    }

    /**
     * Kullanıcı "Login" butonuna tıkladığında tetiklenir.
     * Önce bir request token alır, sonra kullanıcıyı TMDB'nin onay sayfasına yönlendirir.
     */
    private fun handleLoginClick() {
        createRequestTokenUseCase()
            .handleResource(
                onSuccess = { requestToken ->
                    val tmdbAuthUrl = "${AuthConstants.TMDB_AUTHENTICATION_URL}$requestToken?redirect_to=${AuthConstants.REDIRECT_URL}"
                    sendEffect(WelcomeEffect.NavigateToTmdbLogin(url = tmdbAuthUrl))
                }
            )
    }

    /**
     * AuthCallbackHandler'dan gelen onaylanmış token'ları dinler.
     * Bir token geldiğinde, onunla bir session oluşturmaya çalışır.
     */
    private fun listenForAuthCallback() {
        authCallbackHandler.tokenFlow
            .onEach { approvedToken ->
                // Onaylanmış token geldi, şimdi bu token ile session oluştur.
                createSessionUseCase(approvedToken)
                    .handleResource(
                        onSuccess = {
                            // Başarılı! Home ekranına yönlendir.
                            sendEffect(WelcomeEffect.NavigateToHome)
                        }
                    )
            }
            .launchIn(viewModelScope)
    }

    /**
     * Resource akışlarını işlemek için merkezi bir yardımcı extension fonksiyon.
     * Bu, 'when' bloğunu ve state güncellemelerini tek bir yerde toplayarak kod tekrarını önler.
     *
     * @param T Resource içindeki başarılı veri tipi.
     * @param onSuccess Kaynak başarılı olduğunda çalıştırılacak olan eylem.
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
                    // Başarı durumunda, verilen lambda'yı çalıştır.
                    onSuccess(resource.data)
                }
                is Resource.Error -> {
                    setState { copy(isLoading = false, error = resource.exception) }
                }
            }
        }.launchIn(viewModelScope)
    }
}