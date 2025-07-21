package com.mustafakocer.feature_auth.shared.data.repository

import com.mustafakocer.core_common.provider.SessionProvider
import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.core_network.util.safeApiCall
import com.mustafakocer.feature_auth.account.domain.repository.AccountRepository
import com.mustafakocer.feature_auth.shared.data.api.AuthApiService
import com.mustafakocer.feature_auth.shared.data.model.DeleteSessionRequestDto
import com.mustafakocer.feature_auth.shared.data.model.SessionRequestDto
import com.mustafakocer.feature_auth.shared.data.preferences.SessionManager
import com.mustafakocer.feature_auth.welcome.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager,
) : LoginRepository, AccountRepository, SessionProvider {

    // --- LoginRepository Fonksiyonları ---

    override fun createRequestToken(): Flow<Resource<String>> {
        return safeApiCall { authApiService.createRequestToken() }
            .map { resource -> resource.map { it.requestToken } }
    }

    override fun createSession(requestToken: String): Flow<Resource<String>> {
        return safeApiCall { authApiService.createSession(SessionRequestDto(requestToken)) }
            .onEach { resource ->
                if (resource is Resource.Success && resource.data.success) {
                    sessionManager.saveSessionId(resource.data.sessionId)
                }
            }
            .map { resource -> resource.map { it.sessionId } }
    }

    // --- AccountRepository & SessionProvider Fonksiyonları ---

    override fun observeSessionId(): Flow<String?> {
        return sessionManager.sessionIdFlow
    }

    override suspend fun clearSession() {
        // Önce mevcut session'ı al
        val sessionId = sessionManager.sessionIdFlow.first()
        if (!sessionId.isNullOrBlank()) {
            // API'den silmeyi dene (başarısız olsa bile devam et)
            try {
                authApiService.deleteSession(DeleteSessionRequestDto(sessionId))
            } catch (e: Exception) {
                // Hata olursa logla, ama akışı durdurma.
                // Kullanıcının asıl istediği, yerel session'ın silinmesidir.
                e.printStackTrace()
            }
        }
        // Her durumda yerel session'ı temizle
        sessionManager.clearSessionId()
    }
}

// Resource<T> üzerinde çalışan küçük bir yardımcı extension
private fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Success -> Resource.Success(transform(data))
        is Resource.Error -> this
        is Resource.Loading -> Resource.Loading
    }
}