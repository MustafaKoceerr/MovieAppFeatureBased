package com.mustafakocer.core_preferences.provider

import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.core_preferences.repository.SessionManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/**
 * The default, concrete implementation of the [SessionProvider] interface.
 *
 * @param sessionManager The underlying manager responsible for handling session data persistence.
 *
 * Architectural Note:
 * This class acts as the bridge between the abstract `SessionProvider` interface defined in the
 * `core_domain` layer and the concrete data-handling logic provided by the `SessionManager`.
 * By depending on the `SessionManager`, it exposes session state to the rest of the app in a
 * clean, observable, and decoupled manner, fulfilling the contract required by the domain layer.
 * This is a direct application of the Dependency Inversion Principle.
 */
@Singleton
class DefaultSessionProvider @Inject constructor(
    private val sessionManager: SessionManager,
) : SessionProvider {

    override fun observeSessionId(): Flow<String?> {
        return sessionManager.sessionIdFlow
    }
}