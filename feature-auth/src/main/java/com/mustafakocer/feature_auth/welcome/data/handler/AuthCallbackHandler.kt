package com.mustafakocer.feature_auth.welcome.data.handler

import com.mustafakocer.feature_auth.welcome.domain.provider.AuthCallbackProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthCallbackHandler @Inject constructor() : AuthCallbackProvider {
    private val _tokenFlow = MutableSharedFlow<String>(replay = 1)
    override val tokenFlow = _tokenFlow.asSharedFlow()

    fun onNewTokenReceived(token: String) {
        _tokenFlow.tryEmit(token)
    }
}