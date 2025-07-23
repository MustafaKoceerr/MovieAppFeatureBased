package com.mustafakocer.feature_auth.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Defines the Data Transfer Objects (DTOs) used for serialization and deserialization
 * in all authentication-related network requests.
 *
 * Architectural Note:
 * These classes are strictly for data transfer and are part of the data layer's implementation
 * details. They use `@Serializable` for Kotlinx Serialization and `@SerialName` to decouple
 * the Kotlin property names from the exact JSON field names, making the code more resilient
 * to API changes.
 */

// --- Request DTOs ---

/**
 * Represents the request body sent to create a new session.
 */
@Serializable
data class SessionRequestDto(
    @SerialName("request_token") val requestToken: String,
)

/**
 * Represents the request body sent to delete an existing session.
 */
@Serializable
data class DeleteSessionRequestDto(
    @SerialName("session_id") val sessionId: String,
)

// --- Response DTOs ---

/**
 * Represents the response received after a session creation request.
 */
@Serializable
data class SessionDto(
    @SerialName("success") val success: Boolean,
    @SerialName("session_id") val sessionId: String?,
)

/**
 * Represents the response received when requesting a temporary token.
 */
@Serializable
data class RequestTokenDto(
    @SerialName("success") val success: Boolean,
    @SerialName("expires_at") val expiresAt: String,
    @SerialName("request_token") val requestToken: String,
)

/**
 * Represents the response received after a successful session deletion.
 */
@Serializable
data class DeleteSessionResponseDto(
    @SerialName("success") val success: Boolean,
)