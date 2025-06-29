package com.mustafakocer.core_database.pagination

import com.mustafakocer.core_database.cache.CacheService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Pagination business logic service
 *
 * CLEAN ARCHITECTURE: Application/Domain Layer
 * RESPONSIBILITY: Pagination state management and business rules
 *
 * DESIGN PATTERN: Service Layer
 * - Stateless business logic
 * - Injectable service
 * - Coordinates with CacheService
 */
@Singleton
class PaginationService @Inject constructor(
    private val cacheService: CacheService,
) {

    /**
     * Create initial remote key for first page
     */
    fun createInitialKey(
        query: String,
        entityType: String,
        firstPageKey: String? = null,
        totalPages: Int? = null,
        totalItems: Int? = null,
    ): RemoteKey {
        return RemoteKey(
            query = query,
            entityType = entityType,
            currentPage = 1,
            nextKey = firstPageKey,
            prevKey = null,
            totalPages = totalPages,
            totalItems = totalItems,
            cache = cacheService.createCache()
        )
    }

    /**
     * Create next page remote key from current key
     */
    fun createNextPageKey(
        currentKey: RemoteKey,
        nextKey: String?,
        totalPages: Int? = null,
        totalItems: Int? = null,
    ): RemoteKey {
        return currentKey.copy(
            currentPage = currentKey.currentPage + 1,
            nextKey = nextKey,
            prevKey = currentKey.nextKey, // Current next becomes previous
            totalPages = totalPages ?: currentKey.totalPages,
            totalItems = totalItems ?: currentKey.totalItems,
            cache = cacheService.refresh(currentKey.cache)
        )
    }

    /**
     * Create previous page remote key from current key
     */
    fun createPreviousPageKey(
        currentKey: RemoteKey,
        prevKey: String?,
        totalPages: Int? = null,
        totalItems: Int? = null,
    ): RemoteKey {
        return currentKey.copy(
            currentPage = (currentKey.currentPage - 1).coerceAtLeast(1),
            nextKey = currentKey.prevKey, // Current prev becomes next
            prevKey = prevKey,
            totalPages = totalPages ?: currentKey.totalPages,
            totalItems = totalItems ?: currentKey.totalItems,
            cache = cacheService.refresh(currentKey.cache)
        )
    }

    /**
     * Update remote key with new cache metadata
     */
    fun updateCacheMetadata(
        key: RemoteKey,
        newCache: com.mustafakocer.core_database.cache.CacheMetadata,
    ): RemoteKey {
        return key.copy(cache = newCache)
    }

    /**
     * Refresh remote key cache
     */
    fun refreshKey(key: RemoteKey): RemoteKey {
        return key.copy(
            cache = cacheService.refresh(key.cache)
        )
    }

    /**
     * Update pagination totals
     */
    fun updateTotals(
        key: RemoteKey,
        totalPages: Int?,
        totalItems: Int?,
    ): RemoteKey {
        return key.copy(
            totalPages = totalPages,
            totalItems = totalItems
        )
    }

    /**
     * Check if should load next page based on remaining items and prefetch distance
     */
    fun shouldLoadNextPage(
        key: RemoteKey,
        itemsRemaining: Int,
        prefetchDistance: Int = 5,
    ): Boolean {
        return key.hasNextPage &&
                itemsRemaining <= prefetchDistance &&
                !key.cache.isExpired &&
                cacheService.getHealthScore(key.cache) > 0.0
    }

    /**
     * Check if should load previous page
     */
    fun shouldLoadPreviousPage(
        key: RemoteKey,
        itemsRemainingAtStart: Int,
        prefetchDistance: Int = 5,
    ): Boolean {
        return key.hasPreviousPage &&
                itemsRemainingAtStart <= prefetchDistance &&
                !key.cache.isExpired
    }

    /**
     * Check if pagination data needs refresh
     */
    fun needsRefresh(
        key: RemoteKey,
        forceRefresh: Boolean = false,
    ): Boolean {
        return cacheService.shouldRefresh(key.cache, forceRefresh)
    }

    /**
     * Get pagination status
     */
    fun getPaginationStatus(key: RemoteKey): PaginationStatus {
        return when {
            key.cache.isExpired -> PaginationStatus.EXPIRED
            !key.hasNextPage && key.isLastPage -> PaginationStatus.COMPLETE
            key.hasNextPage -> PaginationStatus.HAS_MORE
            key.isFirstPage && !key.hasNextPage -> PaginationStatus.SINGLE_PAGE
            else -> PaginationStatus.PARTIAL
        }
    }

    /**
     * Calculate optimal page size based on total items and target pages
     */
    fun calculateOptimalPageSize(
        totalItems: Int,
        targetPages: Int = 10,
        minPageSize: Int = 10,
        maxPageSize: Int = 100,
    ): Int {
        if (totalItems <= 0 || targetPages <= 0) return minPageSize

        val calculatedSize = totalItems / targetPages
        return calculatedSize.coerceIn(minPageSize, maxPageSize)
    }

    /**
     * Create query for entity with optional parameters
     */
    fun createQuery(
        entityType: String,
        category: String? = null,
        searchTerm: String? = null,
        filters: Map<String, String> = emptyMap(),
    ): String {
        return RemoteKey.createQuery(entityType, category, searchTerm, filters)
    }

    /**
     * Validate pagination parameters
     */
    fun validatePaginationParams(
        page: Int,
        pageSize: Int,
        maxPageSize: Int = 100,
    ): PaginationValidation {
        val errors = mutableListOf<String>()

        if (page < 1) {
            errors.add("Page number must be greater than 0")
        }

        if (pageSize < 1) {
            errors.add("Page size must be greater than 0")
        }

        if (pageSize > maxPageSize) {
            errors.add("Page size cannot exceed $maxPageSize")
        }

        return PaginationValidation(
            isValid = errors.isEmpty(),
            errors = errors,
            correctedPage = page.coerceAtLeast(1),
            correctedPageSize = pageSize.coerceIn(1, maxPageSize)
        )
    }

    /**
     * Reset pagination to first page
     */
    fun resetToFirstPage(key: RemoteKey): RemoteKey {
        return key.copy(
            currentPage = 1,
            prevKey = null,
            cache = cacheService.refresh(key.cache)
        )
    }

    /**
     * Pagination status enum
     */
    enum class PaginationStatus {
        HAS_MORE,      // More pages available
        COMPLETE,      // All pages loaded
        SINGLE_PAGE,   // Only one page total
        PARTIAL,       // Some pages loaded, state unclear
        EXPIRED        // Cache expired, need refresh
    }

    /**
     * Pagination validation result
     */
    data class PaginationValidation(
        val isValid: Boolean,
        val errors: List<String>,
        val correctedPage: Int,
        val correctedPageSize: Int,
    )
}