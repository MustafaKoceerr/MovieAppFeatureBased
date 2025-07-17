package com.mustafakocer.feature_movies.list.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.mustafakocer.core_database.dao.BaseDao
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity

@Dao
interface MovieListDao : BaseDao<MovieListEntity> {

    // PagingSource'un kendisi dile duyarlı hale gelecek, bu yüzden bu sorgu değişebilir.
    // Şimdilik bu sorgunun, PagingSource'un constructor'ına taşınacağını varsayalım.
    @Query("SELECT * FROM movie_list WHERE category = :category AND language = :language ORDER BY position ASC")
    fun getMoviesForCategory(category: String, language: String): PagingSource<Int, MovieListEntity>

    @Query("DELETE FROM movie_list WHERE category = :category AND language = :language")
    suspend fun deleteMoviesForCategory(category: String, language: String): Int

    @Query("SELECT COUNT(*) > 0 FROM movie_list WHERE category = :category AND language = :language")
    suspend fun hasCachedDataForCategory(category: String, language: String): Boolean

    // ... diğer olası sorgular da 'language' parametresi almalı ...
}