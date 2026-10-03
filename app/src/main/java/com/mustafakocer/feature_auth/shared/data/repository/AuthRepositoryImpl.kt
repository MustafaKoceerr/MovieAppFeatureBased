package com.mustafakocer.feature_auth.shared.data.repository

import android.util.Log
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_domain.util.mapSuccess
import com.mustafakocer.core_network.util.safeApiCall
import com.mustafakocer.core_preferences.repository.SessionManager
import com.mustafakocer.feature_auth.account.domain.repository.AccountRepository
import com.mustafakocer.feature_auth.shared.data.api.AuthApiService
import com.mustafakocer.feature_auth.shared.data.model.DeleteSessionRequestDto
import com.mustafakocer.feature_auth.shared.data.model.SessionRequestDto
import com.mustafakocer.feature_auth.welcome.domain.repository.LoginRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * The concrete implementation of `LoginRepository` and `AccountRepository`.
 *
 * @param authApiService The Retrofit service for remote authentication endpoints.
 * @param sessionManager The manager for persisting the session ID locally.
 *
 * Architectural Note:
 * This class serves as the single source of truth for all authentication-related data operations.
 * It implements two separate repository interfaces (`LoginRepository`, `AccountRepository`) to provide
 * role-based contracts to the domain layer, while centralizing the underlying data logic. It is
 * responsible for orchestrating calls between the remote API and the local `SessionManager`.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager,
) : LoginRepository, AccountRepository {

    override fun createRequestToken(): Flow<Resource<String>> {
        return safeApiCall { authApiService.createRequestToken() }
            .map { resource ->
                resource.mapSuccess { dto -> dto.requestToken }
            }
    }

    override fun createSession(requestToken: String): Flow<Resource<String>> {
        return safeApiCall { authApiService.createSession(SessionRequestDto(requestToken)) }
            .onEach { resource ->
                // Architectural Decision:
                // The `onEach` operator is used here to perform a side effect. As soon as a
                // successful response is received, the session ID is immediately saved to local
                // storage. This happens *before* the final data transformation, ensuring that
                // the session is persisted as early as possible in the flow.
                if (resource is Resource.Success) {
                    resource.data.sessionId?.let {
                        sessionManager.saveSessionId(it)
                    }
                }
            }
            .map { resource ->
                resource.mapSuccess { dto -> dto.sessionId ?: "" }
            }
            .map { resource ->
                // Final validation step: If the API call was successful but the session ID is
                // blank, convert this to an error state to be handled by the ViewModel.
                if (resource is Resource.Success && resource.data.isBlank()) {
                    Resource.Error(AppException.Data.Parse(Exception("Session ID from API was null or blank.")))
                } else {
                    resource
                }
            }
    }

    override fun observeSessionId(): Flow<String?> {
        return sessionManager.sessionIdFlow
    }

    override suspend fun logout() {
        // Architectural Decision: "Local-First" Logout
        // The logout process is designed to prioritize the user's local experience.
        // 1.  The local session is cleared *immediately*. This ensures the user is logged out
        //     on their device instantly, even if they are offline or the remote call fails.
        // 2.  The remote session deletion is a "best-effort" or "fire-and-forget" operation.
        //     Its failure is logged but does not impact the user experience, as they are
        //     already logged out from the app's perspective.
        withContext(Dispatchers.IO) {
            val localSessionId = sessionManager.sessionIdFlow.first()

            sessionManager.clearSessionId()

            if (localSessionId != null) {
                try {
                    authApiService.deleteSession(DeleteSessionRequestDto(localSessionId))
                } catch (e: Exception) {
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