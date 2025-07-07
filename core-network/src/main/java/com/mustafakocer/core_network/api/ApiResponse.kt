package com.mustafakocer.core_network.api

import kotlinx.serialization.Serializable


/**
 * TMDB API için pagination response
 */
@Serializable
data class PaginatedResponse<T>(
    val results: List<T> = emptyList(),
    val page: Int = 1,
    val total_pages: Int = 1,
    val total_results: Int = 0,
) {
    val hasNext: Boolean get() = page < total_pages
    val hasPrevious: Boolean get() = page > 1
}

