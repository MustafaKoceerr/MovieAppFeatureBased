package com.mustafakocer.feature_movies.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Genre DTO
 */
@Serializable
data class GenreDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String? = null,
)