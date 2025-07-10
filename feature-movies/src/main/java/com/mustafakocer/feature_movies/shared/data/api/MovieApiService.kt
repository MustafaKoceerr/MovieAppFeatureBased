package com.mustafakocer.feature_movies.shared.data.api

import com.mustafakocer.core_network.api.PaginatedResponse
import com.mustafakocer.feature_movies.shared.data.dto.MovieDto
import com.mustafakocer.feature_movies.details.data.remote.dto.MovieDetailsDto
import com.mustafakocer.feature_movies.shared.data.dto.MovieListDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * CONSOLIDATED: Single API service for all movie operations
 *
 * * ✅ Uses core-network's shared Retrofit instance
 *  * ✅ All movie endpoints in one place
 *  *
 *  * ARCHITECTURE BENEFITS:
 *  * - Single source of truth for movie API
 *  * - Easier maintenance and testing
 *  * - Consistent error handling
 *  * - Shared connection pool and configurations
 */

interface MovieApiService {

    // ==================== HOME SCREEN ENDPOINTS ====================

    /**
     * Get now playing movies (Home screen)
     */
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieDto>>

    /**
     * Get popular movies (Home screen)
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieDto>>

    /**
     * Get top rated movies (Home screen)
     */
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieDto>>

    /**
     * Get upcoming movies (Home screen)
     */
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieDto>>

    // ==================== MOVIE LIST ENDPOINTS (PAGINATION) ====================

    /**
     * Get movies by category with pagination (Movie List screen)
     *
     * @param category now_playing, popular, top_rated, upcoming
     */
    @GET("movie/{category}")
    suspend fun getMoviesByCategory(
        @Path("category") category: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): PaginatedResponse<MovieListDto>

    // ==================== MOVIE DETAILS ENDPOINTS ====================

    /**
     * Get movie details by ID (Details screen)
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
    ): Response<MovieDetailsDto>

    // ==================== SEARCH ENDPOINTS ====================

    /**
     * Search movies by query (Search screen)
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponse<MovieListDto>>

    // ==================== FUTURE ENDPOINTS ====================
    // When needed, add:
    // - Movie videos: GET movie/{movie_id}/videos
    // - Movie credits: GET movie/{movie_id}/credits
    // - Similar movies: GET movie/{movie_id}/similar
    // - Recommendations: GET movie/{movie_id}/recommendations
}