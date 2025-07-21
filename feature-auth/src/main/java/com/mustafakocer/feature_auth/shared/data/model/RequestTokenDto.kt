package com.mustafakocer.feature_auth.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTokenDto(
    @SerialName("success") val success: Boolean,
    @SerialName("expires_at") val expiresAt: String,
    @SerialName("request_token") val requestToken: String,
)