package com.mustafakocer.core_database.pagination

import androidx.paging.PagingConfig

/**
 * Paging 3 konfigürasyonu
 *
 * AMAÇ: Farklı ekranlar için pagination ayarları sağlamak
 * KULLANIM: Repository'de Pager() oluştururken config olarak kullanılır
 */
data class PaginationSettings(
    val pageSize: Int = 20,
    val prefetchDistance: Int = 5,
    val initialLoadSize: Int = 40,
    val enablePlaceholders: Boolean = false,
) {
    /**
     * Paging 3 PagingConfig'e çevir
     */
    fun toPagingConfig(): PagingConfig {
        return PagingConfig(
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            initialLoadSize = initialLoadSize,
            enablePlaceholders = enablePlaceholders
        )
    }

    companion object {
        /**
         * Default ayarlar
         */
        val default = PaginationSettings()

        /**
         * Film listesi için optimal ayarlar
         */
        val movieList = PaginationSettings(
            pageSize = 20,
            prefetchDistance = 5,
            initialLoadSize = 40,
            enablePlaceholders = false
        )

        /**
         * Arama için optimal ayarlar
         */
        val search = PaginationSettings(
            pageSize = 10,
            prefetchDistance = 3,
            initialLoadSize = 20,
            enablePlaceholders = false
        )
    }
}