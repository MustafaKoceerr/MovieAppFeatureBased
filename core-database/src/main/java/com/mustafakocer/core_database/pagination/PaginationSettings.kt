package com.mustafakocer.core_database.pagination

import androidx.paging.PagingConfig

/**
 * Pagination configuration data class
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Configuration
 * RESPONSIBILITY: Centralize pagination settings and provide PagingConfig
 *
 * DESIGN PATTERN: Configuration Object
 * - Immutable configuration
 * - Predefined common configurations
 * - Easy conversion to framework types
 */
data class PaginationSettings(
    val pageSize: Int = DEFAULT_PAGE_SIZE,
    val prefetchDistance: Int = DEFAULT_PREFETCH_DISTANCE,
    val initialLoadSize: Int = pageSize * 2,
    val enablePlaceholders: Boolean = false,
    val maxSize: Int = PagingConfig.MAX_SIZE_UNBOUNDED,
    val jumpThreshold: Int = Int.MIN_VALUE,
) {

    /**
     * Convert to Paging 3 PagingConfig
     */
    fun toPagingConfig(): PagingConfig {
        return PagingConfig(
            pageSize = pageSize,
            prefetchDistance = prefetchDistance,
            initialLoadSize = initialLoadSize,
            enablePlaceholders = enablePlaceholders,
            maxSize = maxSize,
            jumpThreshold = jumpThreshold
        )
    }

    /**
     * Create optimized settings for network-first scenarios
     */
    fun optimizeForNetwork(): PaginationSettings {
        return copy(
            prefetchDistance = (pageSize * 0.3).toInt().coerceAtLeast(3),
            initialLoadSize = pageSize,
            enablePlaceholders = false
        )
    }

    /**
     * Create optimized settings for database-first scenarios
     */
    fun optimizeForDatabase(): PaginationSettings {
        return copy(
            prefetchDistance = (pageSize * 0.5).toInt().coerceAtLeast(5),
            initialLoadSize = pageSize * 3,
            enablePlaceholders = true
        )
    }

    /**
     * Create memory-optimized settings
     */
    fun optimizeForMemory(): PaginationSettings {
        return copy(
            maxSize = pageSize * 10, // Keep only 10 pages in memory
            prefetchDistance = (pageSize * 0.2).toInt().coerceAtLeast(2)
        )
    }

    /**
     * Validate settings and return corrected version if needed
     */
    fun validate(): PaginationSettings {
        return copy(
            pageSize = pageSize.coerceIn(MIN_PAGE_SIZE, MAX_PAGE_SIZE),
            prefetchDistance = prefetchDistance.coerceIn(MIN_PREFETCH_DISTANCE, pageSize),
            initialLoadSize = initialLoadSize.coerceAtLeast(pageSize)
        )
    }

    companion object {
        // Default values
        const val DEFAULT_PAGE_SIZE = 20
        const val DEFAULT_PREFETCH_DISTANCE = 5

        // Limits
        const val MIN_PAGE_SIZE = 1
        const val MAX_PAGE_SIZE = 200
        const val MIN_PREFETCH_DISTANCE = 1

        // Predefined configurations
        val default = PaginationSettings()

        val small = PaginationSettings(
            pageSize = 10,
            prefetchDistance = 3,
            initialLoadSize = 10
        )

        val medium = PaginationSettings(
            pageSize = 20,
            prefetchDistance = 5,
            initialLoadSize = 40
        )

        val large = PaginationSettings(
            pageSize = 50,
            prefetchDistance = 10,
            initialLoadSize = 100
        )

        val extraLarge = PaginationSettings(
            pageSize = 100,
            prefetchDistance = 20,
            initialLoadSize = 200
        )

        // Network optimized
        val networkOptimized = PaginationSettings(
            pageSize = 20,
            prefetchDistance = 3,
            initialLoadSize = 20,
            enablePlaceholders = false
        )

        // Database optimized
        val databaseOptimized = PaginationSettings(
            pageSize = 30,
            prefetchDistance = 10,
            initialLoadSize = 60,
            enablePlaceholders = true
        )

        // Memory optimized
        val memoryOptimized = PaginationSettings(
            pageSize = 15,
            prefetchDistance = 3,
            initialLoadSize = 15,
            maxSize = 150 // 10 pages max
        )

        /**
         * Create custom pagination settings
         */
        fun custom(
            pageSize: Int,
            prefetchDistance: Int = pageSize / 4,
            initialLoadSize: Int = pageSize * 2,
            enablePlaceholders: Boolean = false,
        ): PaginationSettings {
            return PaginationSettings(
                pageSize = pageSize,
                prefetchDistance = prefetchDistance.coerceAtLeast(1),
                initialLoadSize = initialLoadSize.coerceAtLeast(pageSize),
                enablePlaceholders = enablePlaceholders
            ).validate()
        }

        /**
         * Create settings optimized for specific use case
         */
        fun forUseCase(useCase: PaginationUseCase): PaginationSettings {
            return when (useCase) {
                PaginationUseCase.SOCIAL_FEED -> PaginationSettings(
                    pageSize = 15,
                    prefetchDistance = 5,
                    initialLoadSize = 30,
                    enablePlaceholders = false
                )

                PaginationUseCase.SEARCH_RESULTS -> PaginationSettings(
                    pageSize = 10,
                    prefetchDistance = 3,
                    initialLoadSize = 20,
                    enablePlaceholders = false
                )

                PaginationUseCase.CATALOG_BROWSE -> PaginationSettings(
                    pageSize = 25,
                    prefetchDistance = 8,
                    initialLoadSize = 50,
                    enablePlaceholders = true
                )

                PaginationUseCase.CHAT_HISTORY -> PaginationSettings(
                    pageSize = 50,
                    prefetchDistance = 15,
                    initialLoadSize = 100,
                    enablePlaceholders = true
                )

                PaginationUseCase.MEDIA_GALLERY -> PaginationSettings(
                    pageSize = 20,
                    prefetchDistance = 10,
                    initialLoadSize = 40,
                    enablePlaceholders = true
                )
            }
        }
    }

    /**
     * Common pagination use cases
     */
    enum class PaginationUseCase {
        SOCIAL_FEED,     // Instagram, Twitter-like feeds
        SEARCH_RESULTS,  // Search listings
        CATALOG_BROWSE,  // Product catalogs, movie lists
        CHAT_HISTORY,    // Message history
        MEDIA_GALLERY    // Photo/video galleries
    }
}