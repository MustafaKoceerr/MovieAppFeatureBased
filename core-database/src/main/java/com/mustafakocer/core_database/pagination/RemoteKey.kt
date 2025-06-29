package com.mustafakocer.core_database.pagination

import androidx.room.*
import com.mustafakocer.core_database.cache.CacheMetadata

/**
 * Remote pagination key storage
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Storage
 * RESPONSIBILITY: Store pagination state data ONLY (no business logic)
 *
 * DESIGN PATTERN: Value Object + Entity
 * - Immutable data container (except copy operations)
 * - Room entity for database storage
 * - Only computed properties (no side effects)
 */
@Entity(
    tableName = "remote_keys",
    indices = [
        Index(value = ["query"], unique = true),
        Index(value = ["entity_type"]),
        Index(value = ["cached_at"]),

    ]
)
data class RemoteKey(
    @PrimaryKey
    val query: String,  // "movies_popular", "movies_search_batman", etc.

    @ColumnInfo(name = "entity_type")
    val entityType: String,  // "movies", "users", "products", etc.

    @ColumnInfo(name = "current_page")
    val currentPage: Int,

    @ColumnInfo(name = "next_key")
    val nextKey: String?,  // Next page identifier

    @ColumnInfo(name = "prev_key")
    val prevKey: String?,  // Previous page identifier

    @ColumnInfo(name = "total_pages")
    val totalPages: Int? = null,

    @ColumnInfo(name = "total_items")
    val totalItems: Int? = null,

    @Embedded
    val cache: CacheMetadata,
) {
    // ✅ ALLOWED: Pure computed properties (no side effects)

    /**
     * Check if there's a next page available
     */
    val hasNextPage: Boolean
        get() = nextKey != null

    /**
     * Check if there's a previous page available
     */
    val hasPreviousPage: Boolean
        get() = prevKey != null

    /**
     * Check if this is the first page
     */
    val isFirstPage: Boolean
        get() = currentPage == 1

    /**
     * Check if this is the last page (based on total pages or next key)
     */
    val isLastPage: Boolean
        get() = totalPages?.let { currentPage >= it } ?: (nextKey == null)

    /**
     * Get the next page number (current + 1)
     */
    val nextPageNumber: Int
        get() = currentPage + 1

    /**
     * Get the previous page number (current - 1, minimum 1)
     */
    val previousPageNumber: Int
        get() = (currentPage - 1).coerceAtLeast(1)

    /**
     * Check if pagination info is complete
     */
    val isComplete: Boolean
        get() = totalPages != null && totalItems != null

    /**
     * Get progress percentage (0.0 to 1.0) if total pages known
     */
    val progressPercentage: Double
        get() = totalPages?.let { total ->
            if (total > 0) currentPage.toDouble() / total.toDouble() else 0.0
        } ?: 0.0

    companion object {
        /**
         * Create query identifier for specific entity and parameters
         *
         * Examples:
         * - createQuery("movies", "popular") → "movies_popular"
         * - createQuery("movies", "search", "batman") → "movies_search_batman"
         * - createQuery("users", filters = mapOf("role" to "admin")) → "users_role_admin"
         */
        fun createQuery(
            entityType: String,
            category: String? = null,
            searchTerm: String? = null,
            filters: Map<String, String> = emptyMap(),
        ): String {
            val parts = mutableListOf(entityType)

            // Add category if provided
            category?.let { parts.add(it) }

            // Add search term if provided
            searchTerm?.let { parts.add("search_$it") }

            // Add filters
            filters.forEach { (key, value) ->
                parts.add("${key}_$value")
            }

            return parts.joinToString("_").lowercase()
                .replace(" ", "_")
                .replace("-", "_")
        }

        /**
         * Create entity type identifier
         */
        fun createEntityType(baseType: String): String {
            return baseType.lowercase().replace(" ", "_")
        }

        /**
         * Parse query to extract components
         * Returns map with "entityType", "category", "searchTerm", etc.
         */
        fun parseQuery(query: String): Map<String, String> {
            val parts = query.split("_")
            val result = mutableMapOf<String, String>()

            if (parts.isNotEmpty()) {
                result["entityType"] = parts[0]
            }

            if (parts.size > 1) {
                // Look for search term
                val searchIndex = parts.indexOf("search")
                if (searchIndex != -1 && searchIndex + 1 < parts.size) {
                    result["searchTerm"] = parts[searchIndex + 1]
                } else {
                    // Assume second part is category
                    result["category"] = parts[1]
                }
            }
            return result
        }
    }
}