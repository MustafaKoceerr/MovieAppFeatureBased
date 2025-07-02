package com.mustafakocer.feature_movies.details.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    val id: Int,
    val name: String
)