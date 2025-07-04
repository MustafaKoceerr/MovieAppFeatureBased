package com.mustafakocer.core_database.pagination.dao

import androidx.room.*
import com.mustafakocer.core_database.pagination.RemoteKey

/**
 * Data Access Object for RemoteKey entity
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Access
 * RESPONSIBILITY: Database operations for pagination state
 *
 * DESIGN PATTERN: Repository Pattern (DAO Implementation)
 * - Abstract database operations
 * - Type-safe queries
 * - Suspend functions for coroutines
 */

@Dao
interface RemoteKeyDao {

    // ==================== BASIC CRUD OPERATIONS ====================

    /**
     * Get remote key by query identifier
     */
    @Query("SELECT * FROM remote_keys WHERE `query` = :query")
    suspend fun getRemoteKey(query: String): RemoteKey?

    /**
     * Get all remote keys for specific entity type
     */
    @Query("SELECT * FROM remote_keys WHERE entity_type = :entityType ORDER BY current_page ASC")
    suspend fun getRemoteKeysForEntity(entityType: String): List<RemoteKey>

    /**
     * Insert or replace single remote key
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKey(remoteKey: RemoteKey)

    /**
     * Insert or replace multiple remote keys
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKeys(remoteKeys: List<RemoteKey>)

    /**
     * Update existing remote key
     */
    @Update
    suspend fun updateRemoteKey(remoteKey: RemoteKey)

    /**
     * Delete remote key by query
     */
    @Query("DELETE FROM remote_keys WHERE `query` = :query")
    suspend fun deleteRemoteKey(query: String)

    /**
     * Delete all remote keys for entity type
     */
    @Query("DELETE FROM remote_keys WHERE entity_type = :entityType")
    suspend fun deleteRemoteKeysForEntity(entityType: String)

    /**
     * Clear all remote keys
     */
    @Query("DELETE FROM remote_keys")
    suspend fun clearAll(): Int

    // ==================== CACHE MANAGEMENT ====================
    // ✅ FIXED: Updated column names with prefix

    /**
     * Get expired remote keys
     */
    @Query("SELECT * FROM remote_keys WHERE remote_cache_expires_at < :currentTime AND remote_cache_is_persistent = 0")
    suspend fun getExpiredKeys(currentTime: Long = System.currentTimeMillis()): List<RemoteKey>

    /**
     * Delete expired remote keys
     */
    @Query("DELETE FROM remote_keys WHERE remote_cache_expires_at < :currentTime AND remote_cache_is_persistent = 0")
    suspend fun deleteExpiredKeys(currentTime: Long = System.currentTimeMillis()): Int

    /**
     * Get valid (non-expired) remote keys
     */
    @Query("SELECT * FROM remote_keys WHERE (remote_cache_expires_at >= :currentTime OR remote_cache_is_persistent = 1)")
    suspend fun getValidKeys(currentTime: Long = System.currentTimeMillis()): List<RemoteKey>

    /**
     * Get keys that need refresh (80% of cache duration passed)
     */
    @Query("""
        SELECT * FROM remote_keys 
        WHERE (remote_cache_cached_at + (remote_cache_expires_at - remote_cache_cached_at) * 0.8) <= :currentTime 
        AND remote_cache_is_persistent = 0
    """)
    suspend fun getKeysNeedingRefresh(currentTime: Long = System.currentTimeMillis()): List<RemoteKey>

    // ==================== PAGINATION QUERIES ====================

    /**
     * Get remote key for first item (lowest page number)
     */
    @Query("SELECT * FROM remote_keys WHERE entity_type = :entityType ORDER BY current_page ASC LIMIT 1")
    suspend fun getFirstRemoteKey(entityType: String): RemoteKey?

    /**
     * Get remote key for last item (highest page number)
     */
    @Query("SELECT * FROM remote_keys WHERE entity_type = :entityType ORDER BY current_page DESC LIMIT 1")
    suspend fun getLastRemoteKey(entityType: String): RemoteKey?

    /**
     * Get remote key by page number
     */
    @Query("SELECT * FROM remote_keys WHERE entity_type = :entityType AND current_page = :page")
    suspend fun getRemoteKeyByPage(entityType: String, page: Int): RemoteKey?

    /**
     * Get remote keys in page range
     */
    @Query("""
        SELECT * FROM remote_keys 
        WHERE entity_type = :entityType 
        AND current_page >= :startPage 
        AND current_page <= :endPage 
        ORDER BY current_page ASC
    """)
    suspend fun getRemoteKeysInRange(
        entityType: String,
        startPage: Int,
        endPage: Int
    ): List<RemoteKey>

    /**
     * Get remote keys with next page available
     */
    @Query("SELECT * FROM remote_keys WHERE entity_type = :entityType AND next_key IS NOT NULL")
    suspend fun getKeysWithNextPage(entityType: String): List<RemoteKey>

    /**
     * Check if entity has more pages to load
     */
    @Query("SELECT COUNT(*) > 0 FROM remote_keys WHERE entity_type = :entityType AND next_key IS NOT NULL")
    suspend fun hasMorePages(entityType: String): Boolean

    // ==================== STATISTICS AND MONITORING ====================

    /**
     * Get total count of remote keys
     */
    @Query("SELECT COUNT(*) FROM remote_keys")
    suspend fun getTotalKeyCount(): Int

    /**
     * Get count of keys for specific entity type
     */
    @Query("SELECT COUNT(*) FROM remote_keys WHERE entity_type = :entityType")
    suspend fun getKeyCountForEntity(entityType: String): Int

    /**
     * Get count of expired keys
     */
    @Query("SELECT COUNT(*) FROM remote_keys WHERE remote_cache_expires_at < :currentTime AND remote_cache_is_persistent = 0")
    suspend fun getExpiredKeyCount(currentTime: Long = System.currentTimeMillis()): Int

    /**
     * Get oldest cached key timestamp
     */
    @Query("SELECT MIN(remote_cache_cached_at) FROM remote_keys")
    suspend fun getOldestCacheTime(): Long?

    /**
     * Get newest cached key timestamp
     */
    @Query("SELECT MAX(remote_cache_cached_at) FROM remote_keys")
    suspend fun getNewestCacheTime(): Long?

    /**
     * Get average cache age in milliseconds
     */
    @Query("SELECT AVG(:currentTime - remote_cache_cached_at) FROM remote_keys WHERE remote_cache_expires_at >= :currentTime")
    suspend fun getAverageCacheAge(currentTime: Long = System.currentTimeMillis()): Double?

    // ==================== DEBUGGING AND MAINTENANCE ====================

    /**
     * Get recent remote keys (for debugging)
     */
    @Query("SELECT * FROM remote_keys ORDER BY remote_cache_cached_at DESC LIMIT :limit")
    suspend fun getRecentKeys(limit: Int = 10): List<RemoteKey>

    /**
     * Get all remote keys ordered by cache time (for debugging)
     */
    @Query("SELECT * FROM remote_keys ORDER BY remote_cache_cached_at DESC")
    suspend fun getAllKeysOrderedByCacheTime(): List<RemoteKey>

    /**
     * Get distinct entity types
     */
    @Query("SELECT DISTINCT entity_type FROM remote_keys")
    suspend fun getDistinctEntityTypes(): List<String>

    /**
     * Get cache health summary for entity
     */
    @Query("""
        SELECT 
            entity_type,
            COUNT(*) as total_keys,
            SUM(CASE WHEN remote_cache_expires_at < :currentTime AND remote_cache_is_persistent = 0 THEN 1 ELSE 0 END) as expired_keys,
            MIN(remote_cache_cached_at) as oldest_cache,
            MAX(remote_cache_cached_at) as newest_cache
        FROM remote_keys 
        WHERE entity_type = :entityType
        GROUP BY entity_type
    """)
    suspend fun getCacheHealthSummary(
        entityType: String,
        currentTime: Long = System.currentTimeMillis()
    ): CacheHealthSummary?

    /**
     * Cleanup old keys (keep only specified count per entity)
     */
    @Query("""
        DELETE FROM remote_keys 
        WHERE `query` NOT IN (
            SELECT `query` FROM remote_keys 
            WHERE entity_type = :entityType 
            ORDER BY remote_cache_cached_at DESC 
            LIMIT :keepCount
        ) AND entity_type = :entityType
    """)
    suspend fun cleanupOldKeys(entityType: String, keepCount: Int = 10): Int

    /**
     * Cache health summary data class
     */
    data class CacheHealthSummary(
        val entity_type: String,
        val total_keys: Int,
        val expired_keys: Int,
        val oldest_cache: Long,
        val newest_cache: Long
    ) {
        val expiredPercentage: Double
            get() = if (total_keys > 0) expired_keys.toDouble() / total_keys.toDouble() else 0.0

        val cacheSpanHours: Double
            get() = (newest_cache - oldest_cache).toDouble() / (60 * 60 * 1000)
    }
}