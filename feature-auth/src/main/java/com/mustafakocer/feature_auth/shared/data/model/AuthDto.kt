package com.mustafakocer.feature_auth.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- Request DTOs ---

@Serializable
data class SessionRequestDto(
    @SerialName("request_token") val requestToken: String,
)

@Serializable
data class DeleteSessionRequestDto(
    @SerialName("session_id") val sessionId: String, // @SerialName eklemek iyi bir pratiktir
)

// --- Response DTOs ---

@Serializable
data class SessionDto(
    @SerialName("success") val success: Boolean,
    @SerialName("session_id") val sessionId: String?, // API'den null gelebilir, güvenli olalım
)

@Serializable
data class RequestTokenDto(
    @SerialName("success") val success: Boolean,
    @SerialName("expires_at") val expiresAt: String,
    @SerialName("request_token") val requestToken: String,
)

@Serializable
data class DeleteSessionResponseDto(
    @SerialName("success") val success: Boolean,
)