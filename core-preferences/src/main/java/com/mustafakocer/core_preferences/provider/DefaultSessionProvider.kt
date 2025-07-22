package com.mustafakocer.core_preferences.provider

import com.mustafakocer.core_domain.provider.SessionProvider
import javax.inject.Inject
import javax.inject.Singleton
import com.mustafakocer.core_preferences.repository.SessionManager // SessionManager'ı kullanacak
import kotlinx.coroutines.flow.Flow

/**
 * Oturumun durumuna bireden fazla feature'da ihtiyaç duyabiliriz. Bu nedenle core-preferences'a taşıdık.
 */
@Singleton
class DefaultSessionProvider @Inject constructor(
    private val sessionManager: SessionManager,
) : SessionProvider {
    override fun observeSessionId(): Flow<String?> {
        return sessionManager.sessionIdFlow
    }

}