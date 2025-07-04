package com.mustafakocer.feature_movies.list.data.remote

import com.mustafakocer.feature_movies.list.data.remote.dto.MovieListResponse
import com.mustafakocer.feature_movies.list.domain.model.MovieCategory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Movie list API service
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - External Data Source
 * RESPONSIBILITY: Define API endpoints for movie lists
 * */

interface MovieListApiService {
    /**
     * Get movies by category with pagination
     *
     * @param category Movie Category (now_playing, popular, top_rated, upcoming)
     * @param apiKey TMDB API key (injected via DI)
     * @param page Page number (starts from 1)
     * @param language Language code (default: en-US)
     * @param region Region code (optional)
     */

    @GET("movie/{category}")
    suspend fun getMoviesByCategory(
        @Path("category") category: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): MovieListResponse
}