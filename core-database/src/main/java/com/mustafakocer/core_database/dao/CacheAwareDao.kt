package com.mustafakocer.core_database.dao

/**
 * Cache-aware base DAO with cache metadata operations
 *
 * RESPONSIBILITY: Provide cache-specific operations
 */
interface CacheAwareDao<T> : BaseDao<T> {

    /**
     * Get valid (non-expired) entities
     * @param currentTime Current timestamp
     * @return List of valid entities
     */
    suspend fun getValidEntities(currentTime: Long = System.currentTimeMillis()): List<T>

    /**
     * Get expired entities
     * @param currentTime Current timestamp
     * @return List of expired entities
     */
    suspend fun getExpiredEntities(currentTime: Long = System.currentTimeMillis()): List<T>

    /**
     * Delete expired entities
     * @param currentTime Current timestamp
     * @return Number of rows deleted
     */
    suspend fun deleteExpiredEntities(currentTime: Long = System.currentTimeMillis()): Int

    /**
     * Get count of valid entities
     * @param currentTime Current timestamp
     * @return Count of valid entities
     */
    suspend fun getValidEntityCount(currentTime: Long = System.currentTimeMillis()): Int

    /**
     * Get count of expired entities
     * @param currentTime Current timestamp
     * @return Count of expired entities
     */
    suspend fun getExpiredEntityCount(currentTime: Long = System.currentTimeMillis()): Int

    /**
     * Get entities that need refresh (cache aging)
     * @param currentTime Current timestamp
     * @param refreshThreshold Refresh threshold (0.0 to 1.0)
     * @return List of entities needing refresh
     */
    suspend fun getEntitiesNeedingRefresh(
        currentTime: Long = System.currentTimeMillis(),
        refreshThreshold: Double = 0.8,
    ): List<T>
}