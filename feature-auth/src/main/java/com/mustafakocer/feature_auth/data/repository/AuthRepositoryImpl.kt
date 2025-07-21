package com.mustafakocer.feature_auth.data.repository

import android.content.res.Resources
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.exception.toAppException
import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.core_network.util.safeApiCall
import com.mustafakocer.feature_auth.data.api.AuthApiService
import com.mustafakocer.feature_auth.data.model.SessionRequestDto
import com.mustafakocer.feature_auth.data.preferences.SessionManager
import com.mustafakocer.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager,
) : AuthRepository {
    override fun createRequestToken(): Flow<Resource<String>> {
        return safeApiCall {
            authApiService.createRequestToken()
        }.map { resource ->
            // safeApiCall bize Resource<RequestTokenDto> verir.
            // Biz bunu Resource<String>'e dönüştürmeliyiz.
            when (resource) {
                is Resource.Success -> {
                    if (resource.data.success) {
                        Resource.Success(resource.data.requestToken)
                    } else {
                        // TMDB bazen 200 OK ile birlikte "success: false" dönebilir.
                        // Bu durumu özel bir hata olarak ele alıyoruz.
                        Resource.Error(AppException.Unknown(Exception("TMDB API returned success:false")))
                    }
                }

                is Resource.Error -> resource // Hatayı olduğu gibi aktar.
                is Resource.Loading -> Resource.Loading // Yükleme durumunu aktar.
            }
        }
    }

    override fun createSession(requestToken: String): Flow<Resource<String>> {
        return safeApiCall {
            authApiService.createSession(SessionRequestDto(requestToken))
        }.onEach { resource ->
            // .map'ten önce .onEach kullanarak yan etkileri (side-effects) yönetmek
            // Daha temiz bir yaklaşımdır. Akışın bu noktasında session'ı kaydet.
            if (resource is Resource.Success && resource.data.success) {
                sessionManager.saveSessionId(resource.data.sessionId)
            }
        }.map { resource ->
            // Akışı Resource<SessionDto'dan Resource<String>'e dönüştür.
            when (resource) {
                is Resource.Success -> {
                    if (resource.data.success) {
                        Resource.Success(resource.data.sessionId)
                    } else {
                        Resource.Error(AppException.Unknown(Exception("TMDB API returned success:false")))
                    }
                }

                is Resource.Error -> resource
                is Resource.Loading -> Resource.Loading
            }
        }
    }

    override fun observeSessionId(): Flow<String?> {
        return sessionManager.sessionIdFlow
    }

    override suspend fun clearSession() {
        // Gelecekte buraya API'den session'ı silme çağrısı eklenebilir.
        // TODO: Api'den silme çağrısı ekle.
        sessionManager.clearSessionId()
    }
}