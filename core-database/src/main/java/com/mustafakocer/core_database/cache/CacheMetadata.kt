package com.mustafakocer.core_database.cache

import androidx.room.ColumnInfo

/**
 * Basit cache metadata - sadece gerekli alanlar
 *
 * Amaç: Entity'lerin ne zaman cache'lendiğini ve ne zaman expire olacağını saklamak
 * Kullanım: @Embedded olarak entity'lere gömülür.
 */

data class CacheMetadata(
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long,

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long,

    @ColumnInfo(name = "cache_version")
    val cacheVersion: Int = 1,

    @ColumnInfo(name = "is_persistent")
    val isPersistent: Boolean = false,
) {
    companion object {

        /**
         * Yeni cache metadata oluştur
         */
        fun create(durationMillis: Long): CacheMetadata {
            val now = System.currentTimeMillis()
            return CacheMetadata(
                cachedAt = now,
                expiresAt = now + durationMillis,
                cacheVersion = 1,
                isPersistent = false
            )
        }
    }
}