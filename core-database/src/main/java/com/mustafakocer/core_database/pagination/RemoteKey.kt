package com.mustafakocer.core_database.pagination

import androidx.room.*
import com.mustafakocer.core_database.cache.CacheMetadata

/**
 * Paging 3 için sayfa durumu
 *
 * Amaç: RemoteMediator'ın hangi sayfada olduğunu ve daha sayfa var mı bilgisini saklamak
 * KULLANIM: Her kategori için bir RemoteKey (örn: "movies_popular", "movies_top_rated")
 */
@Entity(
    tableName = "remote_keys",
    indices = [Index(value = ["query"], unique = true)]
)
data class RemoteKey(
    @PrimaryKey
    val query: String,  // "movies_popular", "movies_top_rated" vs.

    @ColumnInfo(name = "current_page")
    val currentPage: Int,

    @ColumnInfo(name = "total_pages")
    val totalPages: Int? = null,

    @Embedded(prefix = "cache_")
    val cache: CacheMetadata,
) {
    /**
     * Daha sayfa var mı?
     */
    val hasMorePages: Boolean
        get() = totalPages?.let { currentPage < it } ?: true

    /**
     * İlk sayfa mı?
     */
    val isFirstPage: Boolean
        get() = currentPage == 1

    /**
     * Son sayfa mı?
     */
    val isLastPage: Boolean
        get() = totalPages?.let { currentPage >= it } ?: false

    companion object {
        /**
         * Yeni RemoteKey oluştur
         */
        fun create(
            query: String,
            page: Int = 1,
            totalPages: Int? = null,
        ): RemoteKey {
            return RemoteKey(
                query = query,
                currentPage = page,
                totalPages = totalPages,
                cache = CacheMetadata.oneHour()
            )
        }

        /**
         * Query string oluştur
         * Örnek: createQuery("movies", "popular") -> "movies_popular"
         */
        fun createQuery(entityType: String, category: String): String {
            return "${entityType}_${category}".lowercase()
        }
    }
}