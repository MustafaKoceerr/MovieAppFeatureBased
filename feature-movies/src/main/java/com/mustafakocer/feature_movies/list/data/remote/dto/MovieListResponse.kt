package com.mustafakocer.feature_movies.list.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * API response wrapper for movie lists
 */
@Serializable
data class MovieListResponse(
    val page: Int,
    val results: List<MovieListDto>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)