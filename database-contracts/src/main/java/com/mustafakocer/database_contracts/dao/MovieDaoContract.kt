package com.mustafakocer.database_contracts.dao

import androidx.paging.PagingSource  // ← BU IMPORT EKSİKTİ


// ==================== MOVIE DAO CONTRACT ====================

/**
 * Movie-specific operasyonlar için sözleşme
 * Movie DAO'ları bu arayüzü implement eder
 */
interface MovieDaoContract<T : Any> : CacheAwareDaoContract<T> {
    suspend fun getMoviesForPage(category: String, page: Int): List<T>
    suspend fun deleteMoviesForCategory(category: String): Int
    suspend fun hasCachedDataForCategory(category: String): Boolean
    fun getMoviesPagingSource(category: String): PagingSource<Int, T>

}