package com.mustafakocer.feature_auth.welcome.domain.handler

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * A singleton class that acts as a communication bridge for the authentication callback.
 *
 * Architectural Note:
 * This handler's purpose is to decouple the component that receives the authentication callback
 * (e.g., an Activity handling a deep link) from the ViewModel that needs to process the result.
 * This avoids direct dependencies between components with different lifecycles and provides a
 * robust way to handle asynchronous callbacks. The use of a `SharedFlow` with a replay buffer
 * of 1 is intentional; it ensures that the ViewModel receives the token even if it starts
 * observing *after* the token has been emitted, preventing race conditions.
 */
@Singleton
class AuthCallbackHandler @Inject constructor() {
    private val _tokenFlow = MutableSharedFlow<String>(replay = 1)

    /** A public, read-only flow that emits the approved request token when it's received. */
    val tokenFlow = _tokenFlow.asSharedFlow()

    /**
     * Receives a new token and emits it into the shared flow.
     *
     * This method uses `tryEmit` for a non-suspending, "fire-and-forget" emission,
     * which is suitable for being called from a component like an Activity.
     */
    fun onNewTokenReceived(token: String) {
        _tokenFlow.tryEmit(token)
    }
}