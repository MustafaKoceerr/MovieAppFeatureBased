package com.mustafakocer.feature_movies.data.api

import com.mustafakocer.core_network.api.PaginatedResponse
import com.mustafakocer.feature_movies.data.dto.MovieDetailsDto
import com.mustafakocer.feature_movies.data.dto.MovieDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * TEACHING MOMENT: API Service Interface
 *
 * ✅ Clean Retrofit interface
 * ✅ Suspend functions for coroutines
 * ✅ Response<T> for safeApiCall compatibility
 * ✅ TMDB API endpoint mapping
 */

interface MoviesApiService {

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
     * Get now playing movies
     */
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
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

    /**
     * Get movie details by ID
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
    ): Response<MovieDetailsDto>

    /**
     * Search movies
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieDto>>
}