package com.mustafakocer.feature_movies.shared.data.api

import com.mustafakocer.feature_movies.shared.data.model.MovieDto
import com.mustafakocer.feature_movies.shared.data.model.PaginatedResponseDto
import com.mustafakocer.feature_movies.shared.data.model.moviedetails.MovieDetailsDto
import com.mustafakocer.feature_movies.shared.data.model.moviedetails.SpokenLanguage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    // ==================== HOME SCREEN & LIST SCREEN ENDPOINTS ====================
    // Bu endpoint'ler, içinde MovieDto listesi olan bir PaginatedResponseDto döndürür.

    /**
     * Get now playing movies.
     */
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>

    /**
     * Get popular movies.
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>

    /**
     * Get top rated movies.
     */
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>

    /**
     * Get upcoming movies.
     */
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>

    /**
     * Get movies by a dynamic category with pagination.
     */
    @GET("movie/{category}")
    suspend fun getMoviesByCategory(
        @Path("category") category: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
    ): Response<PaginatedResponseDto<MovieDto>>


    // ==================== SEARCH ENDPOINT ====================
    // Bu endpoint de bir liste döndürdüğü için, aynı PaginatedResponseDto<MovieDto> yapısını kullanır.

    /**
     * Search movies by query.
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>


    // ==================== MOVIE DETAILS ENDPOINT ====================
    // Bu endpoint, tamamen farklı bir yapıya sahip olduğu için kendi özel DTO'sunu kullanır.

    /**
     * Get movie details by ID.
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
    ): Response<MovieDetailsDto>
}