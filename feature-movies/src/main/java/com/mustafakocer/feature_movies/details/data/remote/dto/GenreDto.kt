package com.mustafakocer.feature_movies.details.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GenreDto(
    val id: Int,
    val name: String
)