package com.mustafakocer.feature_auth.shared.data.api

import com.mustafakocer.feature_auth.shared.data.model.DeleteSessionRequestDto
import com.mustafakocer.feature_auth.shared.data.model.DeleteSessionResponseDto
import com.mustafakocer.feature_auth.shared.data.model.RequestTokenDto
import com.mustafakocer.feature_auth.shared.data.model.SessionDto
import com.mustafakocer.feature_auth.shared.data.model.SessionRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST

/**
 * Defines the Retrofit service interface for all authentication-related API endpoints.
 *
 * Architectural Note:
 * This interface provides a type-safe contract for making network calls specific to the
 * authentication feature. It is implemented by Retrofit at runtime.
 */
interface AuthApiService {
    /**
     * Creates a new, temporary request token to initiate the user authentication flow.
     */
    @GET("authentication/token/new")
    suspend fun createRequestToken(): Response<RequestTokenDto>

    /**
     * Creates a new, permanent session ID using an approved request token.
     */
    @POST("authentication/session/new")
    suspend fun createSession(
        @Body sessionRequest: SessionRequestDto,
    ): Response<SessionDto>

    /**
     * Invalidates an existing session ID to log the user out.
     *
     * Architectural Note on `@HTTP`:
     * The TMDB API's `/authentication/session` DELETE endpoint requires a request body, which
     * is a non-standard implementation of the HTTP DELETE method. Retrofit's standard `@DELETE`
     * annotation does not support sending a body. Therefore, the more flexible `@HTTP` annotation
     * is used here to correctly form the request, specifying the method, path, and the
     * presence of a body. This is a pragmatic solution to a specific API quirk.
     */
    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession(
        @Body deleteSessionRequest: DeleteSessionRequestDto,
    ): Response<DeleteSessionResponseDto>
}