package com.mustafakocer.feature_movies.shared.data.api

import com.mustafakocer.feature_movies.shared.data.model.MovieDto
import com.mustafakocer.feature_movies.shared.data.model.MovieDetailsDto
import com.mustafakocer.feature_movies.shared.data.model.PaginatedResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Defines the contract for all network operations related to movies using the Retrofit library.
 *
 * Architectural Decision: This interface abstracts the API endpoints into clear, type-safe Kotlin
 * functions. By using Retrofit annotations (`@GET`, `@Path`, `@Query`), we declare how requests
 * should be made without writing the low-level HTTP client code. Wrapping the return types in
 * `Response<T>` is a deliberate choice that allows the repository layer to inspect the full HTTP
 * response, including status codes and headers, which is essential for robust error handling.
 */
interface MovieApiService {

    // ==================== HOME SCREEN & LIST SCREEN ENDPOINTS ====================
    // These endpoints return a paginated list of movies, all sharing the `PaginatedResponseDto<MovieDto>` structure.

    /**
     * Fetches the current list of movies that are playing in theaters.
     * @param page The page number of the results to fetch.
     * @return A Retrofit [Response] containing a [PaginatedResponseDto] of [MovieDto]s.
     */
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>

    /**
     * Fetches the list of the most popular movies.
     * @param page The page number of the results to fetch.
     * @return A Retrofit [Response] containing a [PaginatedResponseDto] of [MovieDto]s.
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>

    /**
     * Fetches the list of the top-rated movies.
     * @param page The page number of the results to fetch.
     * @return A Retrofit [Response] containing a [PaginatedResponseDto] of [MovieDto]s.
     */
    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>

    /**
     * Fetches the list of upcoming movies.
     * @param page The page number of the results to fetch.
     * @return A Retrofit [Response] containing a [PaginatedResponseDto] of [MovieDto]s.
     */
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>

    /**
     * Fetches movies for a dynamically specified category.
     * This is a more generic version of the specific category endpoints above.
     * @param category The category path segment (e.g., "popular", "top_rated").
     * @param page The page number of the results to fetch.
     * @return A Retrofit [Response] containing a [PaginatedResponseDto] of [MovieDto]s.
     */
    @GET("movie/{category}")
    suspend fun getMoviesByCategory(
        @Path("category") category: String,
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>

    // ==================== SEARCH ENDPOINT ====================
    // This endpoint also returns a paginated list and thus reuses the `PaginatedResponseDto<MovieDto>` structure.

    /**
     * Searches for movies based on a user-provided query.
     * @param query The search term to look for.
     * @param page The page number of the search results to fetch.
     * @return A Retrofit [Response] containing a [PaginatedResponseDto] of [MovieDto]s.
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
    ): Response<PaginatedResponseDto<MovieDto>>

    // ==================== MOVIE DETAILS ENDPOINT ====================
    // This endpoint returns a single, detailed movie object and uses its own specific DTO.

    /**
     * Fetches the full details for a single movie by its ID.
     * @param movieId The unique identifier of the movie to fetch.
     * @return A Retrofit [Response] containing a detailed [MovieDetailsDto].
     */
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
    ): Response<MovieDetailsDto>
}