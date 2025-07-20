package com.mustafakocer.feature_movies.home.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mustafakocer.feature_movies.shared.data.local.entity.HomeMovieEntity

@Dao
interface HomeMovieDao {

    @Upsert
    suspend fun upsertAll(movies: List<HomeMovieEntity>)

    @Query("SELECT * FROM home_movies WHERE category = :category AND language = :language")
    suspend fun getMoviesForCategory(category: String, language: String): List<HomeMovieEntity>

    @Query("DELETE FROM home_movies WHERE category = :category AND language = :language")
    suspend fun deleteMoviesForCategory(category: String, language: String): Int
}