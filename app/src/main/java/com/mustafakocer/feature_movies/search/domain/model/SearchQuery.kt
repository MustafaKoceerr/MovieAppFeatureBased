package com.mustafakocer.feature_movies.search.domain.model

/**
 * Represents a search query as a domain-specific Value Object.
 *
 * Architectural Decision: By encapsulating the raw string query into a `SearchQuery` class,
 * we elevate it from a primitive type to a rich domain model. This object becomes the single
 * source of truth for all business rules related to a search query, such as validation
 * (e.g., minimum/maximum length) and formatting (e.g., trimming whitespace). This approach, known
 * as the Value Object pattern, improves type safety and ensures that other parts of the
 * application (like use cases and repositories) can rely on receiving a valid, pre-processed query,
 * thus simplifying their logic.
 *
 * @property query The raw search string provided by the user.
 * @property minLength The minimum required length for a valid query.
 * @property maxLength The maximum allowed length for a query.
 */
data class SearchQuery(
    val query: String,
    val minLength: Int = 3, // A common minimum length to avoid overly broad/expensive searches.
    val maxLength: Int = 50,
) {
    /**
     * Checks if the query meets the defined business rules for a valid search.
     * This property encapsulates the validation logic directly within the value object.
     */
    val isValid: Boolean
        get() = query.trim().length in minLength..maxLength

    val cleanQuery: String
        get() = query.trim()

    /**
     * Provides factory methods for creating `SearchQuery` instances.
     */
    companion object {

        fun empty() = SearchQuery("")

        fun create(input: String): SearchQuery = SearchQuery(input)
    }
}