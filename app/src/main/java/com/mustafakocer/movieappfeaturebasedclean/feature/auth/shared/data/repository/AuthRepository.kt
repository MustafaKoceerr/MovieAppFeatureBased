package com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.repository

import android.util.Log
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_domain.util.mapSuccess
import com.mustafakocer.core_network.util.safeApiCall
import com.mustafakocer.core_preferences.repository.SessionManager
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.api.AuthApiService
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.model.DeleteSessionRequestDto
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.model.SessionRequestDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * Handles the TMDB authentication flow (request token -> session) and the stored session.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager,
) : SessionProvider {

    /** Step 1 of the login flow: a request token the user then approves on the TMDB website. */
    fun createRequestToken(): Flow<Resource<String>> =
        safeApiCall { authApiService.createRequestToken() }
            .map { resource -> resource.mapSuccess { dto -> dto.requestToken } }

    /** Step 2: exchanges the approved token for a session id and stores it locally. */
    fun createSession(requestToken: String): Flow<Resource<String>> =
        safeApiCall { authApiService.createSession(SessionRequestDto(requestToken)) }
            .onEach { resource ->
                if (resource is Resource.Success) {
                    resource.data.sessionId?.let { sessionManager.saveSessionId(it) }
                }
            }
            .map { resource -> resource.mapSuccess { dto -> dto.sessionId ?: "" } }
            .map { resource ->
                if (resource is Resource.Success && resource.data.isBlank()) {
                    Resource.Error(
                        AppException.Data.Parse(Exception("Session ID from API was null or blank."))
                    )
                } else {
                    resource
                }
            }

    override fun observeSessionId(): Flow<String?> = sessionManager.sessionIdFlow

    /** Clears the local session first, then makes a best-effort call to delete it on TMDB. */
    suspend fun logout() {
        withContext(Dispatchers.IO) {
            val localSessionId = sessionManager.sessionIdFlow.first()
            sessionManager.clearSessionId()
            if (localSessionId != null) {
                try {
                    authApiService.deleteSession(DeleteSessionRequestDto(localSessionId))
                } catch (e: Exception) {
                    Log.w(TAG, "Remote session deletion failed, but user is logged out locally.", e)
                }
            }
        }
    }

    private companion object {
        const val TAG = "AuthRepository"
    }
}
