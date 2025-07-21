package com.mustafakocer.feature_auth.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteSessionRequestDto(
    val session_id: String
)