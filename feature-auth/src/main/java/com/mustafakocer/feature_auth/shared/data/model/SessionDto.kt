package com.mustafakocer.feature_auth.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionDto(
    @SerialName("success") val success: Boolean,
    @SerialName("session_id") val sessionId: String,
)