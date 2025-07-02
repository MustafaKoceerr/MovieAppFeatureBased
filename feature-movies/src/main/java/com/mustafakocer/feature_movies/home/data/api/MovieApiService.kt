package com.mustafakocer.feature_movies.home.data.api

import com.mustafakocer.core_network.api.PaginatedResponse
import com.mustafakocer.feature_movies.home.data.dto.MovieDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * TEACHING MOMENT: Specific API Endpoints
 *
 * ✅ BENEFITS:
 * - Each endpoint explicitly defined
 * - Clear API contract
 * - Easy to test individual endpoints
 * - Better error handling per endpoint
 * - No enum-based string manipulation
 */
interface MovieApiService {

    // ==================== MOVIE CATEGORIES ====================

    /**
     * Get now playing movies
     */
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieDto>>

    /**
     * Get popular movies
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieDto>>

    /**
     * Get top rated movies
     */
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieDto>>

    /**
     * Get upcoming movies
     */
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieDto>>



    // ==================== SEARCH ====================

    /**
     * Search movies by query
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieDto>>
}