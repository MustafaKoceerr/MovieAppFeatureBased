package com.mustafakocer.database_contracts.dao

/**
 * Cache-aware DAO contract
 * For DAOs that handle cached entities
 */
interface CacheAwareDaoContract<T> : BaseDaoContract<T> {

    /**
     * Get valid (non-expired) entities
     */
    suspend fun getValidEntities(currentTime: Long= System.currentTimeMillis()): List<T>

    /**
     * Delete expired entities
     */
    suspend fun deleteExpiredEntities(currentTime: Long = System.currentTimeMillis()): Int

    /**
     * Check if has valid cached data
     */
    suspend fun hasValidData(currentTime: Long = System.currentTimeMillis()): Boolean
}