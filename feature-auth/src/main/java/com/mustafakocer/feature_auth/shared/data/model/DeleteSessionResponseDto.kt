package com.mustafakocer.feature_auth.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteSessionResponseDto(
    val success: Boolean,
)