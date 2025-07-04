package com.mustafakocer.core_database.dao

import androidx.room.*

/**
 * Base DAO implementation with common CRUD operations
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Core Data Access
 * RESPONSIBILITY: Provide common database operations for all entities
 */
@Dao
interface BaseDao<T> {

    /**
     * Insert single entity
     * @return Generated ID
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: T): Long

    /**
     * Insert multiple entities
     * @return List of generated IDs
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<T>): List<Long>

    /**
     * Update single entity
     * @return Number of rows updated
     */
    @Update
    suspend fun update(entity: T): Int

    /**
     * Delete single entity
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(entity: T): Int

    /**
     * Upsert single entity (Insert or Update)
     * Modern Room approach for conflict handling
     */
    @Upsert
    suspend fun upsert(entity: T)

    /**
     * Upsert multiple entities
     * Efficient batch operation
     */
    @Upsert
    suspend fun upsertAll(entities: List<T>)
}