package com.mustafakocer.feature_movies.domain.repository

import com.mustafakocer.core_common.UiState
import com.mustafakocer.feature_movies.domain.model.Movie
import com.mustafakocer.feature_movies.domain.model.MovieCategory
import com.mustafakocer.feature_movies.domain.model.MovieDetails
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query

/**
 * TEACHING MOMENT: Repository Pattern
 *
 * DOMAIN LAYER'DA:
 * ✅ Interface tanımı (implementation data layer'da)
 * ✅ Domain models kullanır
 * ✅ UiState döndürür
 * ✅ Business logic'e odaklanır
 */

interface MoviesRepository {
    /**
     * Get movies by category with pagination
     */
    fun getMoviesByCategory(
        category: MovieCategory,
        page: Int = 1,
    ): Flow<UiState<List<Movie>>>

    /**
     * Get movie details by ID
     */
    fun getMovieDetails(movieId: Int): Flow<UiState<MovieDetails>>

    /**
     * Search movies by query
     */
    fun searchMovies(
        query: String,
        page: Int = 1,
    ): Flow<UiState<List<Movie>>>
}