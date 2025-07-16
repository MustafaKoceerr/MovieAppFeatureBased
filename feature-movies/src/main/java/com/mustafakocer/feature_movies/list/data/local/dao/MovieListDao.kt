package com.mustafakocer.feature_movies.list.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.mustafakocer.core_database.dao.BaseDao
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity

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
        SELECT * FROM movie_list 
        WHERE category = :category 
        AND (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
        ORDER BY position ASC
    """
    )
    fun getMoviesPagingSource(
        category: String,
        currentTime: Long = System.currentTimeMillis(),
    ): PagingSource<Int, MovieListEntity>

    /**
     * Delete movies for specific category
     */
    @Query("DELETE FROM movie_list WHERE category = :category")
    suspend fun deleteMoviesForCategory(category: String): Int

    /**
     * Check if category has cached data
     */
    @Query(
        """
        SELECT COUNT(*) > 0 FROM movie_list 
        WHERE category = :category
        AND (movie_cache_is_persistent = 1 OR movie_cache_expires_at > :currentTime)
    """
    )
    suspend fun hasCachedDataForCategory(
        category: String,
        currentTime: Long = System.currentTimeMillis(),
    ): Boolean

}