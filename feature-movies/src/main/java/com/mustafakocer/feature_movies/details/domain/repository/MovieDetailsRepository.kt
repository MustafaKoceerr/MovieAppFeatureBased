package com.mustafakocer.feature_movies.details.domain.repository

import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails
import kotlinx.coroutines.flow.Flow

/**
 * Movie details repository interface
 *
 * CLEAN ARCHITECTURE: Domain Layer
 * RESPONSIBILITY: Define movie details data operations contract
 */
interface MovieDetailsRepository {

    /**
     * Get movie details by ID from API
     *
     * @param movieId The movie ID
     * @return Flow of UiState with movie details
     */
    fun getMovieDetails(movieId: Int): Flow<UiState<MovieDetails>>
}