//package com.mustafakocer.feature_movies.list.data.local.dao
//
//import androidx.room.Dao
//import androidx.room.Query
//import com.mustafakocer.core_database.dao.BaseDao
//import com.mustafakocer.core_database.pagination.RemoteKey
//
///**
// * Remote key DAO for movie list pagination
// *
// * CLEAN ARCHITECTURE: Infrastructure Layer - Data Access
// * USES: Core database RemoteKey entity
// * EXTENDS: Core BaseDao for basic CRUD operations
// */
//@Dao
//interface MovieListRemoteKeyDao : BaseDao<RemoteKey> {
//
//    /**
//     * Get remote key for specific category
//     * Used by RemoteMediator to determine pagination state
//     */
//    @Query("SELECT * FROM remote_keys WHERE `query` = :categoryKey")
//    suspend fun getRemoteKeyForCategory(categoryKey: String): RemoteKey?
//
//    /**
//     * Delete remote key for specific category
//     * Used during refresh operations
//     */
//    @Query("DELETE FROM remote_keys WHERE `query` = :categoryKey")
//    suspend fun deleteRemoteKeyForCategory(categoryKey: String): Int
//
//    /**
//     * Delete all remote keys
//     * Used for complete data reset
//     */
//    @Query("DELETE FROM remote_keys")
//    suspend fun clearAllRemoteKeys(): Int
//
//    /**
//     * Get all remote keys for debugging
//     * Useful for cache state inspection
//     */
//    @Query("SELECT * FROM remote_keys ORDER BY cache_cached_at DESC")
//    suspend fun getAllRemoteKeys(): List<RemoteKey>
//
//    /**
//     * Delete expired remote keys
//     * Automatic cleanup based on core database expiration
//     */
//    @Query(
//        """
//        DELETE FROM remote_keys
//        WHERE cache_expires_at < :expirationTime
//    """
//    )
//    suspend fun deleteExpiredRemoteKeys(
//        expirationTime: Long = System.currentTimeMillis() - (24 * 60 * 60 * 1000L), // 24 hours ago
//    ): Int
//
//    /**
//     * Check if category has valid remote key
//     */
//    @Query(
//        """
//        SELECT COUNT(*) > 0 FROM remote_keys
//        WHERE `query` = :categoryKey
//        AND cache_expires_at > :currentTime
//    """
//    )
//    suspend fun hasValidRemoteKey(
//        categoryKey: String,
//        currentTime: Long = System.currentTimeMillis(),
//    ): Boolean
//}