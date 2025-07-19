package com.mustafakocer.feature_movies.details.domain.repository

import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import kotlinx.coroutines.flow.Flow

interface MovieDetailsRepository {

    /**
     * Get movie details by ID from API
     *
     * @param movieId The movie ID
     * @return Flow of UiState with movie details
     */
    fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetails>>
}