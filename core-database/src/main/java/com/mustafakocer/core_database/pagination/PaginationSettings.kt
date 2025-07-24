package com.mustafakocer.core_database.pagination

import androidx.paging.PagingConfig

/**
 * A configuration class that defines settings for the Paging 3 library.
 *
 * @param pageSize The number of items to load in each page.
 * @param prefetchDistance The distance from the end of the loaded content at which to start prefetching more items.
 * @param initialLoadSize The number of items to load for the initial page. It's often larger than `pageSize`.
 * @param enablePlaceholders Determines if `null` placeholders should be displayed for unloaded items.
 *
 * Architectural Note:
 * This dedicated class centralizes all pagination configurations. Instead of hardcoding values
 * when creating a `Pager`, we use instances of this class. This ensures consistency and allows
 * for creating tailored, reusable configurations for different UI scenarios (e.g., browsing vs. searching).
 */
data class PaginationSettings(
    val pageSize: Int = 20,
    val prefetchDistance: Int = 5,
    val initialLoadSize: Int = 40,
    val enablePlaceholders: Boolean = false,
) {
    /**
     * Converts this settings object into a [PagingConfig] instance for use with the Paging 3 library.
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
         * Defines distinct use cases for pagination to apply tailored settings.
         */
        enum class PaginationUseCase {
            CATALOG_BROWSING,

            SEARCH_RESULTS,
        }

        fun forUseCase(useCase: PaginationUseCase): PaginationSettings {
            return when (useCase) {
                PaginationUseCase.CATALOG_BROWSING -> PaginationSettings(
                    pageSize = 20,
                    prefetchDistance = 5,
                    initialLoadSize = 40
                )

                PaginationUseCase.SEARCH_RESULTS -> PaginationSettings(
                    pageSize = 10,
                    prefetchDistance = 3,
                    initialLoadSize = 20
                )
            }
        }

        /** Default pagination settings, suitable for general catalog browsing. */
        val default = forUseCase(PaginationUseCase.CATALOG_BROWSING)

        /** Pre-configured settings optimized for browsing movie lists. */
        val forCatalog = forUseCase(PaginationUseCase.CATALOG_BROWSING)

        /** Pre-configured settings optimized for displaying search results. */
        val forSearch = forUseCase(PaginationUseCase.SEARCH_RESULTS)
    }
}