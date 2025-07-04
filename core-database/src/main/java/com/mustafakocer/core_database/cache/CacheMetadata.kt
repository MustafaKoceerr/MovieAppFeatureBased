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
        // Cache süreleri (milliseconds)
        const val HOUR_1 = 60 * 60 * 1000L        // 1 saat
        const val HOUR_24 = 24 * 60 * 60 * 1000L  // 24 saat

        /**
         * Yeni cache metadata oluştur
         */
        fun create(durationMillis: Long = HOUR_1): CacheMetadata {
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
        fun oneHour(): CacheMetadata = create(HOUR_1)

        /**
         * 24 saatlik cache
         */
        fun oneDay(): CacheMetadata = create(HOUR_24)
    }
}