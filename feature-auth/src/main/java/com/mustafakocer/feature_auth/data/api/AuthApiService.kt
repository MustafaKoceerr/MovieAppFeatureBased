package com.mustafakocer.feature_auth.data.api

import com.mustafakocer.feature_auth.data.model.RequestTokenDto
import com.mustafakocer.feature_auth.data.model.SessionDto
import com.mustafakocer.feature_auth.data.model.SessionRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @GET("authentication/token/new")
    suspend fun createRequestToken(): Response<RequestTokenDto>

    @POST("authentication/session/new")
    suspend fun createSession(
        @Body sessionRequest: SessionRequestDto
    ): Response<SessionDto>
}