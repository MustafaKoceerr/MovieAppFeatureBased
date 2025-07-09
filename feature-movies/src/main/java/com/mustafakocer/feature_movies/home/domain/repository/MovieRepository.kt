package com.mustafakocer.feature_movies.home.domain.repository

import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.feature_movies.shared.domain.model.Movie
import kotlinx.coroutines.flow.Flow

/**
 * TEACHING MOMENT: Individual Category Repository
 *
 * ✅ SIMPLIFIED APPROACH:
 * - Home just delegates to MovieRepository
 * - No complex parallel loading
 * - ViewModel handles coordination
 */
interface MovieRepository {

    // ==================== MOVIE CATEGORIES ====================

    /**
     * Get now playing movies
     */
    fun getNowPlayingMovies(page: Int = 1): Flow<UiState<List<Movie>>>

    /**
     * Get popular movies
     */
    fun getPopularMovies(page: Int = 1): Flow<UiState<List<Movie>>>

    /**
     * Get top rated movies
     */
    fun getTopRatedMovies(page: Int = 1): Flow<UiState<List<Movie>>>

    /**
     * Get upcoming movies
     */
    fun getUpcomingMovies(page: Int = 1): Flow<UiState<List<Movie>>>

}