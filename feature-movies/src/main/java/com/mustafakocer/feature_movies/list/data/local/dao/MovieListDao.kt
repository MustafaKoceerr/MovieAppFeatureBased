package com.mustafakocer.feature_movies.list.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.mustafakocer.core_database.dao.BaseDao
import com.mustafakocer.feature_movies.list.data.local.entity.MovieListEntity

/**
 * Movie list DAO extending core database capabilities
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Access
 * EXTENDS: Core BaseDao for basic CRUD operations
 */
@Dao
interface MovieListDao : BaseDao<MovieListEntity> {

    // ==================== MOVIE DAO CONTRACT IMPLEMENTATIONS ====================

    /**
     * Get paging source for specific category
     * Primary method for Paging 3 integration
     */
    @Query(
        """
        SELECT * FROM movie_list_items 
        WHERE category = :category 
        AND (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
        ORDER BY page ASC, position ASC
    """
    )
    fun getMoviesPagingSource(
        category: String,
        currentTime: Long = System.currentTimeMillis(),
    ): PagingSource<Int, MovieListEntity>

    /**
     * Get movies for specific page
     */
    @Query(
        """
        SELECT * FROM movie_list_items 
        WHERE category = :category 
        AND page = :page
        AND (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
        ORDER BY position ASC
    """
    )
    suspend fun getMoviesForPage(
        category: String,
        page: Int,
        currentTime: Long = System.currentTimeMillis(),
    ): List<MovieListEntity>

    /**
     * Delete movies for specific category
     */
    @Query("DELETE FROM movie_list_items WHERE category = :category")
    suspend fun deleteMoviesForCategory(category: String): Int

    /**
     * Check if category has cached data
     */
    @Query(
        """
        SELECT COUNT(*) > 0 FROM movie_list_items 
        WHERE category = :category
        AND (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
    """
    )
    suspend fun hasCachedDataForCategory(
        category: String,
        currentTime: Long = System.currentTimeMillis(),
    ): Boolean

    /**
     * Get last page for category
     */
    @Query(
        """
        SELECT MAX(page) FROM movie_list_items 
        WHERE category = :category
        AND (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
    """
    )
    suspend fun getLastPageForCategory(
        category: String,
        currentTime: Long = System.currentTimeMillis(),
    ): Int?

    // ==================== CACHE AWARE CONTRACT IMPLEMENTATIONS ====================

    /**
     * Get valid entities
     */
    @Query(
        """
        SELECT * FROM movie_list_items 
        WHERE (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
        ORDER BY movie_cache_cached_at DESC
    """
    )
    suspend fun getValidEntities(currentTime: Long = System.currentTimeMillis()): List<MovieListEntity>

    /**
     * Delete expired entities
     */
    @Query(
        """
        DELETE FROM movie_list_items 
        WHERE movie_cache_is_persistent = 0 
        AND movie_cache_expires_at <= :currentTime
    """
    )
    suspend fun deleteExpiredEntities(currentTime: Long = System.currentTimeMillis()): Int

    /**
     * Check if has valid data
     */
    @Query(
        """
        SELECT COUNT(*) > 0 FROM movie_list_items 
        WHERE (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
    """
    )
    suspend fun hasValidData(currentTime: Long = System.currentTimeMillis()): Boolean
}