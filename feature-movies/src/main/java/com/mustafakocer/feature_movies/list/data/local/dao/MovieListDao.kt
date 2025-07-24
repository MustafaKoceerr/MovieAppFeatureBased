package com.mustafakocer.feature_movies.list.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.mustafakocer.core_database.dao.BaseDao
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity

/**
 * Data Access Object (DAO) for managing the paginated list of movie entities.
 *
 * This interface defines the database operations required for the Paging 3 library's
 * `RemoteMediator` to function correctly. It provides methods to query a `PagingSource`,
 * clear cached data, and check for the existence of data.
 */
@Dao
interface MovieListDao : BaseDao<MovieListEntity> {

    @Query("SELECT * FROM movie_list WHERE category = :category AND language = :language ORDER BY position ASC")
    fun getMoviesForCategory(category: String, language: String): PagingSource<Int, MovieListEntity>

    @Query("DELETE FROM movie_list WHERE category = :category AND language = :language")
    suspend fun deleteMoviesForCategory(category: String, language: String): Int

    @Query("SELECT COUNT(*) > 0 FROM movie_list WHERE category = :category AND language = :language")
    suspend fun hasCachedDataForCategory(category: String, language: String): Boolean
}