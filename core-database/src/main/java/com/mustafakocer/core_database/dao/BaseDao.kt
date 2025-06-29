package com.mustafakocer.core_database.dao

import androidx.room.*

/**
 * Base DAO interface with common CRUD operations
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Access
 * RESPONSIBILITY: Provide common database operations for all entities
 *
 * DESIGN PATTERN: Template Method Pattern
 * - Common interface for all DAOs
 * - Reusable CRUD operations
 * - Type-safe generic operations
 */

interface BaseDao<T> {
    /**
     * Insert single entity
     * @param entity Entity to insert
     * @return Row ID of inserted entity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T): Long

    /**
     * Inser multiple entities
     * @param entities List of entities to insert
     * @return List of row IDs
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<T>): List<Long>

    /**
     * Insert single entity, ignore if conflict
     * @param entity Entity to insert
     * @return Row ID or -1 if conflict
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(entity: T): Long

    /**
     * Insert multiple entities, ignore conflicts
     * @param entities List of entities to insert
     * @return List of row IDs (-1 for conflicts)
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(entities: List<T>): List<Long>

    /**
     * Update single entity
     * @param entity Entity to update
     * @return Number of rows updated
     */
    @Update
    suspend fun update(entity: T): Int

    /**
     * Update multiple entities
     * @param entities List of entities to update
     * @return Number of rows updated
     */
    @Update
    suspend fun updateAll(entity: List<T>): Int

    /**
     * Delete single entity
     * @param entity Entity to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun delete(entity: T): Int

    /**
     * Delete multiple entities
     * @param entities List of entities to delete
     * @return Number of rows deleted
     */
    @Delete
    suspend fun deleteAll(entities: List<T>): Int

    /**
     * Upsert single entity (insert or update)
     * @param entity Entity to upsert
     */
    @Upsert
    suspend fun upsert(entity: T)

    /**
     * Upsert multiple entities (insert or update)
     * @param entities List of entities to upsert
     */
    @Upsert
    suspend fun UpsertAll(entities: List<T>)
}