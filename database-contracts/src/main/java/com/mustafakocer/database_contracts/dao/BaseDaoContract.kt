package com.mustafakocer.database_contracts.dao

/**
 * Temel CRUD operasyonları için sözleşme
 * Tüm DAO'lar bu arayüzü implement eder
 */
interface BaseDaoContract<T : Any> {
    suspend fun insert(entity: T): Long
    suspend fun insertAll(entities: List<T>): List<Long>
    suspend fun update(entity: T): Int
    suspend fun delete(entity: T): Int
    suspend fun upsert(entity: T)
    suspend fun upsertAll(entities: List<T>)
}
