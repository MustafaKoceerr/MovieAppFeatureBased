package com.mustafakocer.feature_movies.search.domain.model

/**
 * Search query model for domain layer
 *
 * CLEAN ARCHITECTURE: Domain Layer
 * RESPONSIBILITY: Encapsulate search business logic
 */
data class SearchQuery(
    val query: String,
    val minLength: Int = 2,
    val maxLength: Int = 50,
) {
    /**
     * Validate search query
     */
    val isValid: Boolean
        get() = query.trim().length >= minLength && query.trim().length <= maxLength

    /**
     * Get cleaned query for API
     */
    val cleanQuery: String
        get() = query.trim()

    companion object {
        fun empty() = SearchQuery("")

        fun create(input: String): SearchQuery = SearchQuery(input)
    }
}

