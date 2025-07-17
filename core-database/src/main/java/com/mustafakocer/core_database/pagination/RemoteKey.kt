package com.mustafakocer.core_database.pagination

import androidx.room.*
import com.mustafakocer.core_database.cache.CacheDuration
import com.mustafakocer.core_database.cache.CacheMetadata

/**
 * Paging 3 için sayfa durumu
 *
 * Amaç: RemoteMediator'ın hangi sayfada olduğunu ve daha sayfa var mı bilgisini saklamak
 * KULLANIM: Her kategori için bir RemoteKey (örn: "movies_popular", "movies_top_rated")
 */
@Entity(
    tableName = "remote_keys",
    primaryKeys = ["query", "language"] // <-- BİLEŞİK ANAHTAR
)data class RemoteKey(
    val query: String,
    val language: String, // <-- YENİ SÜTUN

    @ColumnInfo(name = "entity_type") val entityType: String,
    @ColumnInfo(name = "current_page") val currentPage: Int,
    @ColumnInfo(name = "next_key") val nextKey: String? = null,
    @ColumnInfo(name = "prev_key") val prevKey: String? = null,
    @ColumnInfo(name = "total_pages") val totalPages: Int? = null,
    @ColumnInfo(name = "total_items") val totalItems: Int? = null,
    @Embedded(prefix = "cache_") val cache: CacheMetadata,
) {
    companion object {
        /**
         * Yeni bir RemoteKey nesnesi oluşturur.
         */
        fun create(
            query: String,
            language: String, // <-- YENİ PARAMETRE
            currentPage: Int,
            totalPages: Int?,
            totalItems: Int?,
            cacheDuration: Long = CacheDuration.HOURS_24, // Varsayılan cache süresi
        ): RemoteKey {
            val isEnd = totalPages?.let { currentPage >= it } == true
            return RemoteKey(
                query = query,
                language = language, // <-- YENİ ALANI ATA
                entityType = query.substringBefore('_'), // "movies_popular" -> "movies"
                currentPage = currentPage,
                nextKey = if (isEnd) null else (currentPage + 1).toString(),
                prevKey = if (currentPage == 1) null else (currentPage - 1).toString(),
                totalPages = totalPages,
                totalItems = totalItems,
                cache = CacheMetadata.create(cacheDuration) // Core'daki diğer yardımcıyı kullanıyoruz!
            )
        }

        /**
         * Birden fazla parçadan oluşan birleşik bir anahtar oluşturur.
         * Örnek: createCompositeKey("movies", "popular") -> "movies_popular"
         */
        fun createCompositeKey(vararg parts: String): String {
            return parts.joinToString("_").lowercase()
        }
    }
}