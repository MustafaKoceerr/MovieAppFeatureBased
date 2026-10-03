package com.mustafakocer.feature_auth.account.domain.usecase

import com.mustafakocer.core_domain.provider.SessionProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * A use case dedicated to observing the user's session state.
 *
 * @param sessionProvider The provider that supplies the session data stream.
 *
 * Architectural Note:
 * This use case acts as a clean, focused entry point for the ViewModel to access session
 * information. It abstracts the specific source of the data (the `SessionProvider`) and adheres
 * to the single responsibility principle. The `invoke` operator allows for a more concise and
 * idiomatic call syntax from the ViewModel (e.g., `observeSessionUseCase()`).
 */
class ObserveSessionUseCase @Inject constructor(
    private val sessionProvider: SessionProvider,
) {
    /**
     * @return A [Flow] that emits the session ID string, or null if no session exists.
     */
    operator fun invoke(): Flow<String?> {
        return sessionProvider.observeSessionId()
    }
}