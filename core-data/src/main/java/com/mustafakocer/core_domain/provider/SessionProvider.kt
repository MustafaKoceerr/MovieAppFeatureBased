package com.mustafakocer.core_domain.provider

import kotlinx.coroutines.flow.Flow

/**
 * Provides centralized, observable access to the user's session state.
 *
 * Architectural Note:
 * This interface lives in the 'core_domain' layer to decouple feature modules from the
 * 'feature-auth' module. It allows any feature to react to session changes without needing
 * a direct dependency on the authentication implementation.
 */
interface SessionProvider {
    /**
     * Observes the current session ID.
     * @return A [Flow] that emits the session ID string, or null if the user is logged out.
     */
    fun observeSessionId(): Flow<String?>
}