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
    /**
     * Cache hala geçerli mi?
     */
    val isValid: Boolean
        get() = System.currentTimeMillis() < expiresAt

    /**
     * Cache expire olmuş mu?
     */
    val isExpired: Boolean
        get() = !isValid

    /**
     * Cache kaç dakika eski?
     */
    val ageMinutes: Long
        get() = (System.currentTimeMillis() - cachedAt) / (60 * 1000L)

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

        /**
         * 1 saatlik cache
         */
        fun oneHour(): CacheMetadata = create(CacheDuration.HOURS_24)

        /**
         * 24 saatlik cache
         */
        fun oneDay(): CacheMetadata = create(CacheDuration.HOURS_24)
    }
}