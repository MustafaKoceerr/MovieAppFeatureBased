package com.mustafakocer.database_contracts.dao

import androidx.paging.PagingSource
import com.mustafakocer.database_contracts.entities.MovieEntityContract

/**
 * Movie DAO contract
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer contract
 * RESPONSIBILITY: Define movie data access operations
 */
interface MovieDaoContract<T : MovieEntityContract> : CacheAwareDaoContract<T> {

    /**
     * Get paging source for specific category
     * Core pagination for Paging3
     */
    fun getMoviesPagingSource(
        category: String,
        currentTime: Long = System.currentTimeMillis(),
    ): PagingSource<Int, T>

    /**
     * Get movies for specific page
     */
    suspend fun getMoviesForPage(
        category: String,
        page: Int,
        currentTime: Long = System.currentTimeMillis(),
    ): List<T>

    /**
     * Delete movies for specific category
     */
    suspend fun deleteMoviesForCategory(category: String): Int

    /**
     * Check if category has cached data
     */
    suspend fun hasCachedDataForCategory(
        category: String,
        currentTime: Long = System.currentTimeMillis(),
    ): Boolean

    /**
     * Get last page for category
     */
    suspend fun getLastPageForCategory(
        category: String,
        currentTime: Long = System.currentTimeMillis()
    ): Int?
}