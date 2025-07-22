package com.mustafakocer.feature_auth.welcome.domain.handler

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthCallbackHandler @Inject constructor() {
    private val _tokenFlow = MutableSharedFlow<String>(replay = 1)
    val tokenFlow = _tokenFlow.asSharedFlow() // <-- public property

    fun onNewTokenReceived(token: String) {
        _tokenFlow.tryEmit(token)
    }
}