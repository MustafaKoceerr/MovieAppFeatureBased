package com.mustafakocer.database_contracts.dao

// ==================== CACHE AWARE DAO CONTRACT ====================

/**
 * Cache-aware operasyonlar için sözleşme
 * Cache'li entity'lerle çalışan DAO'lar için
 */
interface CacheAwareDaoContract<T: Any> : BaseDaoContract<T> {
    suspend fun getValidEntities(currentTime: Long = System.currentTimeMillis()): List<T>
    suspend fun deleteExpiredEntities(currentTime: Long = System.currentTimeMillis()): Int
    suspend fun hasValidData(currentTime: Long = System.currentTimeMillis()): Boolean
}