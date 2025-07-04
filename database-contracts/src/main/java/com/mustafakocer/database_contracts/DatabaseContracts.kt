package com.mustafakocer.database_contracts


/**
 * Public API for core-database-contracts module
 *
 * DATABASE CONTRACTS PATTERN:
 * ✅ Single entry point for module consumers
 * ✅ Clear API surface
 * ✅ Easy imports for features
 * ✅ Consistent with navigation-contracts pattern
 *
 * USAGE:
 * import com.mustafakocer.core_database_contracts.*
 */

// ==================== ENTITY CONTRACTS ====================

// Base entity contracts - Re-export for easy consumption
typealias BaseEntityContract = com.mustafakocer.database_contracts.entities.BaseEntityContract
typealias CacheAwareEntityContract = com.mustafakocer.database_contracts.entities.CacheAwareEntityContract
typealias PageableEntityContract = com.mustafakocer.database_contracts.entities.PageableEntityContract

// Feature-specific entity contracts
typealias MovieEntityContract = com.mustafakocer.database_contracts.entities.MovieEntityContract


// ==================== DAO CONTRACTS ====================

// Base DAO contracts - Re-export for easy consumption
typealias BaseDaoContract<T> = com.mustafakocer.database_contracts.dao.BaseDaoContract<T>
typealias CacheAwareDaoContract<T> = com.mustafakocer.database_contracts.dao.CacheAwareDaoContract<T>

// Feature-specific DAO contracts
typealias MovieDaoContract<T> = com.mustafakocer.database_contracts.dao.MovieDaoContract<T>


// ==================== REGISTRY CONTRACTS ====================

// Database composition contracts - Re-export for easy consumption
typealias DatabaseEntityRegistry = com.mustafakocer.database_contracts.registry.DatabaseEntityRegistry
typealias FeatureDatabaseContributor = com.mustafakocer.database_contracts.registry.FeatureDatabaseContributor


// ==================== FACTORY HELPERS ====================

/**
 * Helper object for database contract operations
 *
 * USAGE:
 *  * - DatabaseContractFactory.createMovieEntity(...)
 *  * - DatabaseContractFactory.validateCacheData(...)
 */
object DatabaseContractFactory {

    /**
     * Validate cache-aware entity
     */
    fun validateCacheData(entity: CacheAwareEntityContract): Boolean {
        return entity.isValid
    }

    /**
     * Check if entity needs refresh based on threshold
     */
    fun needsRefresh(
        entity: CacheAwareEntityContract,
        refreshThreshold: Double = 0.8,
    ): Boolean {
        if (entity.isPersistent) return false

        val totalCacheTime = entity.expiresAt - entity.cachedAt
        val elapsedTime = System.currentTimeMillis() - entity.cachedAt
        val usageRatio = elapsedTime.toDouble() / totalCacheTime.toDouble()

        return usageRatio >= refreshThreshold
    }

    /**
     * Get cache age in hours
     */
    fun getCacheAgeHours(entity: CacheAwareEntityContract): Long {
        return (System.currentTimeMillis() - entity.cachedAt) / (60 * 60 * 1000L)
    }

    /**
     * Create category identifier for pageable entities
     */
    fun createCategoryKey(category: String, page: Int): String {
        return "${category}_page_${page}"
    }
}


// ==================== EXTENSION FUNCTIONS ====================

/**
 * Check if cache-aware entity is expired
 */
val CacheAwareEntityContract.isExpired: Boolean
    get() = !isValid

/**
 * Get cache age in minutes
 */
val CacheAwareEntityContract.cacheAgeMinutes: Long
    get() = (System.currentTimeMillis() - cachedAt) / (60 * 1000L)

/**
 * Get cache age in hours
 */
val CacheAwareEntityContract.cacheAgeHours: Long
    get() = (System.currentTimeMillis() - cachedAt) / (60 * 60 * 1000L)

/**
 * Get pageable entity unique key
 */
val PageableEntityContract.pageKey: String
    get() = "${category}_${page}_${position}"

/**
 * Check if movie entity has poster
 */
val MovieEntityContract.hasPoster: Boolean
    get() = !posterPath.isNullOrEmpty()

/**
 * Check if movie entity has backdrop
 */
val MovieEntityContract.hasBackdrop: Boolean
    get() = !backdropPath.isNullOrEmpty()

/**
 * Get movie entity display title (fallback to original title)
 */
val MovieEntityContract.displayTitle: String
    get() = title.ifEmpty { originalTitle }