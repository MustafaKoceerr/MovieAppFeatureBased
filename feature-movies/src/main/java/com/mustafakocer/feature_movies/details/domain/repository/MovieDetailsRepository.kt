package com.mustafakocer.feature_movies.details.domain.repository

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for fetching detailed information for a single movie.
 *
 * Architectural Note:
 * This repository interface lives in the domain layer to provide a clean contract for use cases.
 * It abstracts the data source, allowing the domain logic to be independent of whether the data
 * comes from a network API, a local database, or a combination of both. The data layer is
 * responsible for providing the concrete implementation.
 */
interface MovieDetailsRepository {
    /**
     * Retrieves the details for a specific movie.
     *
     * @param movieId The unique identifier of the movie.
     * @return A [Flow] emitting a [Resource] that wraps the [MovieDetails] on success.
     */
    fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetails>>
}