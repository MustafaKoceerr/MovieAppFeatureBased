package com.mustafakocer.database_contracts.entities

/**
 * Cache aware entity contract
 * For entities that support caching metadata
 */
interface CacheAwareEntityContract: BaseEntityContract {
    val cachedAt: Long
    val expiresAt: Long
    val cacheVersion: Int
    val isPersistent: Boolean

    /**
     * Check if entity is valid (not expired)
     */
    val isValid: Boolean
        get() = isPersistent || System.currentTimeMillis() < expiresAt
}