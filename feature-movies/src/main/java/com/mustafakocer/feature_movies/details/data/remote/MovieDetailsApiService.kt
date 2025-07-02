package com.mustafakocer.feature_movies.details.data.remote

import com.mustafakocer.feature_movies.details.data.remote.dto.MovieDetailsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Movie details API service
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer
 * RESPONSIBILITY: Define API endpoints for movie details
 */
interface MovieDetailsApiService {

    /**
     * Get movie details by ID
     *
     * @param movieId The movie ID
     * @param apiKey TMDB API key
     * @param language Language for the response
     * @return Response with movie details DTO
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US"
    ): Response<MovieDetailsDto>
}