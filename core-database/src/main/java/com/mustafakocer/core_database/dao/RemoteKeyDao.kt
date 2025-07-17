package com.mustafakocer.core_database.dao

import androidx.room.Dao
import androidx.room.Query
import com.mustafakocer.core_database.pagination.RemoteKey

/**
 * Core RemoteKey DAO for pagination state management
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Core Data Access
 * RESPONSIBILITY: Manage pagination state across all features
 */
@Dao
interface RemoteKeyDao : BaseDao<RemoteKey> {

    /**
     * Get remote key by query
     * Used by RemoteMediator to determine pagination state
     */
    @Query("SELECT * FROM remote_keys WHERE `query` = :query AND `language` = :language")
    suspend fun getRemoteKey(query: String, language: String): RemoteKey?

    /**
     * Delete remote key by query
     * Used during refresh operations
     */
    @Query("DELETE FROM remote_keys WHERE `query` = :query AND `language` = :language")
    suspend fun deleteRemoteKey(query: String, language: String): Int

    /**
     * Delete all remote keys
     * Used for complete data reset
     */
    @Query("DELETE FROM remote_keys")
    suspend fun clearAllRemoteKeys(): Int

    /**
     * Delete expired remote keys
     * Automatic cleanup based on core database expiration
     */
    @Query("DELETE FROM remote_keys WHERE cache_expires_at <= :expirationTime")
    suspend fun deleteExpiredRemoteKeys(
        expirationTime: Long = System.currentTimeMillis()
    ): Int


}