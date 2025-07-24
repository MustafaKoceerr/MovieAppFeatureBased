package com.mustafakocer.core_database.pagination

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.mustafakocer.core_database.cache.CacheDuration
import com.mustafakocer.core_database.cache.CacheMetadata

/**
 * Represents the pagination state for a specific remote data query in the Paging 3 library.
 *
 * This entity is used by a `RemoteMediator` to track which page to fetch next for a given
 * list of data (e.g., "popular movies" or "top-rated TV shows").
 *
 * @param query A unique identifier for the specific list being paginated (e.g., "movies_popular").
 * @param language The language of the query, forming part of the composite primary key.
 * @param entityType A general type derived from the query (e.g., "movies"), useful for bulk operations.
 * @param currentPage The index of the most recently fetched page.
 * @param nextKey The key (e.g., page number) for fetching the next page of data. Null if at the end.
 * @param prevKey The key for fetching the previous page of data.
 * @param totalPages The total number of pages available from the remote source.
 * @param totalItems The total number of items available from the remote source.
 * @param cache The embedded metadata for managing the cache lifecycle of this key.
 */
@Entity(
    tableName = "remote_keys",
    primaryKeys = ["query", "language"]
)
data class RemoteKey(
    val query: String,
    val language: String,

    @ColumnInfo(name = "entity_type") val entityType: String,
    @ColumnInfo(name = "current_page") val currentPage: Int,
    @ColumnInfo(name = "next_key") val nextKey: String? = null,
    @ColumnInfo(name = "prev_key") val prevKey: String? = null,
    @ColumnInfo(name = "total_pages") val totalPages: Int? = null,
    @ColumnInfo(name = "total_items") val totalItems: Int? = null,

    // Architectural Note: Embedding cache metadata directly associates the pagination
    // state's lifecycle with its validity, allowing for easy cleanup of stale keys.
    @Embedded(prefix = "cache_") val cache: CacheMetadata,
) {
    companion object {
        /**
         * A factory method to create a new [RemoteKey] instance.
         *
         * @param query The unique identifier for the paginated list.
         * @param language The language code for the query.
         * @param currentPage The current page number received from the API.
         * @param totalPages The total pages reported by the API.
         * @param totalItems The total items reported by the API.
         * @param cacheDuration The desired cache validity period for this key.
         * @return A new, fully configured [RemoteKey] instance.
         */
        fun create(
            query: String,
            language: String,
            currentPage: Int,
            totalPages: Int?,
            totalItems: Int?,
            cacheDuration: Long = CacheDuration.TWENTY_FOUR_HOURS_MS,
        ): RemoteKey {
            val isEndOfList = totalPages?.let { currentPage >= it } == true
            return RemoteKey(
                query = query,
                language = language,
                // The entity type is derived for potential generic operations (e.g., clear all 'movie' keys).
                entityType = query.substringBefore('_'),
                currentPage = currentPage,
                nextKey = if (isEndOfList) null else (currentPage + 1).toString(),
                prevKey = if (currentPage == 1) null else (currentPage - 1).toString(),
                totalPages = totalPages,
                totalItems = totalItems,
                cache = CacheMetadata.create(cacheDuration)
            )
        }

        /**
         * Creates a standardized composite key string from multiple parts.
         * Example: `createCompositeKey("movies", "popular")` returns `"movies_popular"`.
         *
         * @param parts The string components to join.
         * @return A single, lowercased key string.
         */
        fun createCompositeKey(vararg parts: String): String {
            return parts.joinToString("_").lowercase()
        }
    }
}