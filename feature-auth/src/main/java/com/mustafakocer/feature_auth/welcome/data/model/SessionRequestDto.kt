package com.mustafakocer.feature_auth.welcome.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionRequestDto(
    @SerialName("request_token") val requestToken: String,
)