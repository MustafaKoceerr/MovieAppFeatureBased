package com.mustafakocer.movieappfeaturebasedclean.feature.auth.welcome.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.util.AuthConstants
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.welcome.domain.handler.AuthCallbackHandler
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.welcome.domain.usecase.CreateRequestTokenUseCase
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.welcome.domain.usecase.CreateSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class WelcomeUiState(
    val isLoading: Boolean = false,
    val error: AppException? = null,
    /** TMDB approval page; the route opens it and calls [WelcomeViewModel.onLoginUrlHandled]. */
    val loginUrl: String? = null,
    /** True once a session was created; the route navigates to home. */
    val loggedIn: Boolean = false,
)

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val createRequestTokenUseCase: CreateRequestTokenUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val authCallbackHandler: AuthCallbackHandler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    init {
        // The TMDB redirect delivers the approved token through AuthCallbackActivity.
        authCallbackHandler.tokenFlow
            .onEach { approvedToken ->
                createSessionUseCase(approvedToken).collectResource {
                    _uiState.update { it.copy(loggedIn = true) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onLoginClick() {
        createRequestTokenUseCase().collectResource { requestToken ->
            val url = "${AuthConstants.TMDB_AUTHENTICATION_URL}$requestToken" +
                "?redirect_to=${AuthConstants.REDIRECT_URL}"
            _uiState.update { it.copy(loginUrl = url) }
        }
    }

    fun onLoginUrlHandled() {
        _uiState.update { it.copy(loginUrl = null) }
    }

    private fun <T> Flow<Resource<T>>.collectResource(onSuccess: (T) -> Unit) {
        onEach { resource ->
            when (resource) {
                is Resource.Loading -> _uiState.update { it.copy(isLoading = true, error = null) }
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess(resource.data)
                }
                is Resource.Error ->
                    _uiState.update { it.copy(isLoading = false, error = resource.exception) }
            }
        }.launchIn(viewModelScope)
    }
}
