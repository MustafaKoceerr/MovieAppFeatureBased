package com.mustafakocer.core_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert

/**
 * A generic Data Access Object (DAO) that defines a contract for common database operations.
 *
 * @param T The entity type this DAO operates on.
 *
 * Architectural Note:
 * By creating a generic `BaseDao`, we establish a consistent API for all data access operations
 * across the application. Concrete DAOs can inherit from this interface to eliminate boilerplate
 * code for standard Create, Read, Update, Delete (CRUD) actions. This promotes code reuse and
 * simplifies the persistence layer.
 */
@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<T>): List<Long>

    @Update
    suspend fun update(entity: T): Int

    @Delete
    suspend fun delete(entity: T): Int

    @Upsert
    suspend fun upsert(entity: T)

    @Upsert
    suspend fun upsertAll(entities: List<T>)
}