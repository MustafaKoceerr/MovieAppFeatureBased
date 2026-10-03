package com.mustafakocer.feature_auth.welcome.domain.repository

import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.core_domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for handling the user login process.
 *
 * Architectural Note:
 * This repository interface outlines the necessary steps for the TMDB authentication flow.
 * It lives in the domain layer to provide a clean contract for use cases, which are then
 * implemented in the data layer. This separation of concerns ensures that the business logic
 * is independent of the data source implementation.
 */
interface LoginRepository : SessionProvider{
    /**
     * Initiates the login flow by requesting a temporary token from the backend.
     *
     * @return A [Flow] emitting a [Resource] that wraps the request token string on success.
     */
    fun createRequestToken(): Flow<Resource<String>>

    /**
     * Exchanges an approved request token for a permanent session ID.
     *
     * @param requestToken The token approved by the user via the web flow.
     * @return A [Flow] emitting a [Resource] that wraps the final session ID string on success.
     */
    fun createSession(requestToken: String): Flow<Resource<String>>
}