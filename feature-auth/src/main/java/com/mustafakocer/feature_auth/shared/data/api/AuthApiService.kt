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

interface AuthApiService {
    @GET("authentication/token/new")
    suspend fun createRequestToken(): Response<RequestTokenDto>

    @POST("authentication/session/new")
    suspend fun createSession(
        @Body sessionRequest: SessionRequestDto,
    ): Response<SessionDto>

    /**
     * Çıkış Yapma: Mevcut session_id'yi geçersiz kılar.
     * Not: TMDB API'si DELETE metodunda Body'ye izin verdiği için @HTTP kullanıyoruz.
     */
    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession(
        @Body deleteSessionRequest: DeleteSessionRequestDto,
    ): Response<DeleteSessionResponseDto>
}