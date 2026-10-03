package com.mustafakocer.core_database.dao

import androidx.room.Dao
import androidx.room.Query
import com.mustafakocer.core_database.pagination.RemoteKey

/**
 * A Data Access Object (DAO) for managing [RemoteKey] entities, which are essential for
 * handling pagination state with Jetpack's Paging 3 library.
 *
 * Architectural Note:
 * This DAO is a core component of the pagination system. It allows the `RemoteMediator` to
 * persist and retrieve the next page key for a given data query, enabling seamless loading
 * of subsequent pages. It is designed to be feature-agnostic by using a `query` string as
 * a unique identifier for different paginated lists.
 */
@Dao
interface RemoteKeyDao : BaseDao<RemoteKey> {

    @Query("SELECT * FROM remote_keys WHERE `query` = :query AND `language` = :language")
    suspend fun getRemoteKey(query: String, language: String): RemoteKey?

    @Query("DELETE FROM remote_keys WHERE `query` = :query AND `language` = :language")
    suspend fun deleteRemoteKey(query: String, language: String): Int

    @Query("DELETE FROM remote_keys")
    suspend fun clearAllRemoteKeys(): Int

    @Query("DELETE FROM remote_keys WHERE cache_expires_at <= :expirationTime")
    suspend fun deleteExpiredRemoteKeys(
        expirationTime: Long = System.currentTimeMillis(),
    ): Int
}