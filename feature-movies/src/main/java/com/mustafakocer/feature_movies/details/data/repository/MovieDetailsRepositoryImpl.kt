package com.mustafakocer.feature_movies.details.data.repository

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_domain.util.mapSuccess
import com.mustafakocer.core_network.util.safeApiCall
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.data.mapper.toDomain
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The concrete implementation of the [MovieDetailsRepository] interface.
 *
 * @param movieApiService The Retrofit service for remote movie data endpoints.
 *
 * Architectural Note:
 * This repository is responsible for fetching movie details from the network. Its key design
 * features are:
 * 1.  **Centralized API Call Logic:** It uses the `safeApiCall` utility from the `:core_network`
 *     module. This delegates all the boilerplate logic for state emission (`Loading`, `Success`,
 *     `Error`) and exception handling to a central, reusable function.
 * 2.  **Clean Data Mapping:** It uses the `.mapSuccess` extension function to transform the
 *     network DTO (`MovieDetailsDto`) into a domain model (`MovieDetails`). This transformation
 *     only occurs on a successful API response, and the logic is cleanly separated into a
 *     dedicated mapper function (`toDomain`), adhering to the single responsibility principle.
 */
@Singleton
class MovieDetailsRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService,
) : MovieDetailsRepository {

    override fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetails>> =
        safeApiCall {
            movieApiService.getMovieDetails(movieId)
        }.map { resource ->
            resource.mapSuccess { movieDetailsDto ->
                movieDetailsDto.toDomain()
            }
        }
}