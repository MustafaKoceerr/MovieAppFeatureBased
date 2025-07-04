package com.mustafakocer.database_contracts.dao

/**
 * Base DAO contract for all database operations
 *
 * DATABASE CONTRACTS PATTERN:
 * ✅ Common CRUD operations
 * ✅ Type-safe operations
 * ✅ Reactive data access
 */
interface BaseDaoContract<T> {

    /**
     * Insert single entity
     */
    suspend fun insert(entity: T): Long

    /**
     * Insert Multiple entities
     */
    suspend fun insertAll(entities: List<T>): List<Long>

    /**
     * Update entity
     */
    suspend fun update(entity: T): Int

    /**
     * Delete entity
     */
    suspend fun delete(entity: T): Int

    /**
     * Upsert entity
     */
    suspend fun upsert(entity: T)

    /**
     * Upsert multiple entities
     */
    suspend fun upsertAll(entities: List<T>)
}