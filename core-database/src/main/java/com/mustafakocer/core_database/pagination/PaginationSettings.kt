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
         * Use Case bazlı pagination ayarları
         */
        enum class PaginationUseCase {
            CATALOG_BROWSE,    // Film listesi, kategori browse
            SEARCH_RESULTS,    // Arama sonuçları
        }

        /**
         * Use case'e göre optimal ayarları döndür
         */
        fun forUseCase(useCase: PaginationUseCase): PaginationSettings {
            return when (useCase) {
                PaginationUseCase.CATALOG_BROWSE -> PaginationSettings(
                    pageSize = 20,
                    prefetchDistance = 5,
                    initialLoadSize = 40,
                    enablePlaceholders = false
                )
                PaginationUseCase.SEARCH_RESULTS -> PaginationSettings(
                    pageSize = 10,
                    prefetchDistance = 3,
                    initialLoadSize = 20,
                    enablePlaceholders = false
                )
            }
        }

        /**
         * Default ayarlar
         */
        val default = forUseCase(PaginationUseCase.CATALOG_BROWSE)

        /**
         * Film listesi için optimal ayarlar
         */
        val movieList = forUseCase(PaginationUseCase.CATALOG_BROWSE)

        /**
         * Arama için optimal ayarlar
         */
        val search = forUseCase(PaginationUseCase.SEARCH_RESULTS)
    }
}