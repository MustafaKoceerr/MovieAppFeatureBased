package com.mustafakocer.feature_movies.list.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.mustafakocer.core_database.dao.BaseDao
import com.mustafakocer.database_contracts.MovieDaoContract
import com.mustafakocer.feature_movies.list.data.local.entity.MovieListEntity

/**
 * Movie list DAO extending core database capabilities
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Access
 * EXTENDS: Core BaseDao for basic CRUD operations
 * */

@Dao
interface MovieListDao : BaseDao<MovieListEntity>, MovieDaoContract<MovieListEntity> {

    // ==================== CONTRACT IMPLEMENTATIONS ====================
    // ✅ FIXED: Updated all column names with movie_cache_ prefix

    /**
     * Get paging source for specific category
     * Implements MovieDaoContract interface
     */
    @Query(
        """
        SELECT * FROM movie_list_items 
        WHERE category = :category 
        AND (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
        ORDER BY page ASC, position ASC
    """
    )
    override fun getMoviesPagingSource(
        category: String,
        currentTime: Long,
    ): PagingSource<Int, MovieListEntity>

    /**
     * Get movies for specific page
     * Implements MovieDaoContract interface
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
    override suspend fun getMoviesForPage(
        category: String,
        page: Int,
        currentTime: Long,
    ): List<MovieListEntity>

    /**
     * Delete movies for specific category
     * Implements MovieDaoContract interface
     */
    @Query("DELETE FROM movie_list_items WHERE category = :category")
    override suspend fun deleteMoviesForCategory(category: String): Int

    /**
     * Check if category has cached data
     * Implements MovieDaoContract interface
     */
    @Query(
        """
        SELECT COUNT(*) > 0 FROM movie_list_items 
        WHERE category = :category
        AND (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
    """
    )
    override suspend fun hasCachedDataForCategory(
        category: String,
        currentTime: Long,
    ): Boolean

    /**
     * Get last page for category
     * Implements MovieDaoContract interface
     */
    @Query(
        """
        SELECT MAX(page) FROM movie_list_items 
        WHERE category = :category
        AND (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
    """
    )
    override suspend fun getLastPageForCategory(
        category: String,
        currentTime: Long,
    ): Int?

    // ==================== CACHE AWARE CONTRACT IMPLEMENTATIONS ====================

    /**
     * Get valid entities
     * Implements CacheAwareDaoContract interface
     */
    @Query(
        """
        SELECT * FROM movie_list_items 
        WHERE (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
        ORDER BY movie_cache_cached_at DESC
    """
    )
    override suspend fun getValidEntities(currentTime: Long): List<MovieListEntity>

    /**
     * Delete expired entities
     * Implements CacheAwareDaoContract interface
     */
    @Query(
        """
        DELETE FROM movie_list_items 
        WHERE movie_cache_is_persistent = 0 
        AND movie_cache_expires_at <= :currentTime
    """
    )
    override suspend fun deleteExpiredEntities(currentTime: Long): Int

    /**
     * Check if has valid data
     * Implements CacheAwareDaoContract interface
     */
    @Query(
        """
        SELECT COUNT(*) > 0 FROM movie_list_items 
        WHERE (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
    """
    )
    override suspend fun hasValidData(currentTime: Long): Boolean
}