package com.mustafakocer.core_network.api

import kotlinx.serialization.Serializable

// # Generic API response wrapper
/**
 * TEACHING MOMENT: Generic API Response Wrapper
 *
 * Bu wrapper çoğu REST API'nin standard formatını karşılar
 * TMDB API için özellikle pagination response'ları
 */

data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val success: Boolean = true,
    val code: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)

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

