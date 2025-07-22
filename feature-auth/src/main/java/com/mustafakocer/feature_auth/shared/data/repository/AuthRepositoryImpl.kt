package com.mustafakocer.feature_auth.shared.data.repository

import android.util.Log
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_network.util.safeApiCall
import com.mustafakocer.feature_auth.account.domain.repository.AccountRepository
import com.mustafakocer.feature_auth.shared.data.api.AuthApiService
import com.mustafakocer.core_preferences.repository.SessionManager
import com.mustafakocer.feature_auth.welcome.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import com.mustafakocer.core_domain.util.mapSuccess
import com.mustafakocer.feature_auth.shared.data.model.DeleteSessionRequestDto
import com.mustafakocer.feature_auth.shared.data.model.SessionRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager,
) : LoginRepository, AccountRepository {

    // --- LoginRepository Fonksiyonları ---


    override fun createRequestToken(): Flow<Resource<String>> {
        return safeApiCall { authApiService.createRequestToken() }
            .map { resource ->
                resource.mapSuccess { dto ->
                    dto.requestToken
                }
            }
    }

    override fun createSession(requestToken: String): Flow<Resource<String>> {
        return safeApiCall { authApiService.createSession(SessionRequestDto(requestToken)) }
            .onEach { resource ->
                if (resource is Resource.Success) {
                    resource.data.sessionId?.let {
                        sessionManager.saveSessionId(it)
                    }
                }
            }
            .map { resource ->
                // Dönüşüm: Resource<SessionDto> -> Resource<String>
                resource.mapSuccess { dto -> dto.sessionId ?: "" }
            }
            .map { resource ->
                // Doğrulama: Eğer dönüşüm sonrası string boş ise, bu bir hatadır.
                if (resource is Resource.Success && resource.data.isBlank()) {
                    Resource.Error(AppException.Data.Parse(Exception("Session ID from API was null or blank.")))
                } else {
                    resource
                }
            }
    }

    // --- AccountRepository & SessionProvider Fonksiyonları ---

    override fun observeSessionId(): Flow<String?> {
        return sessionManager.sessionIdFlow
    }

    override suspend fun logout() {
        // Bu bir suspend fonksiyon olduğu için, işlemleri IO thread'inde yapmalıyız.
        withContext(Dispatchers.IO) {
            val localSessionId = sessionManager.sessionIdFlow.first()

            // 1. En kritik adım: Yerel oturumu hemen temizle.
            sessionManager.clearSessionId()

            // 2. Eğer yerel oturum varsa, sunucudaki oturumu silmeyi dene.
            if (localSessionId != null) {
                try {
                    authApiService.deleteSession(DeleteSessionRequestDto(localSessionId))
                } catch (e: Exception) {
                    // Hata durumunda, sadece logla. Kullanıcı deneyimini etkilememeli.
                    // Çünkü yerel çıkış zaten yapıldı.
                    Log.w(
                        "AuthRepositoryImpl",
                        "Remote session deletion failed, but user is logged out locally.",
                        e
                    )
                }
            }
        }
    }

}
