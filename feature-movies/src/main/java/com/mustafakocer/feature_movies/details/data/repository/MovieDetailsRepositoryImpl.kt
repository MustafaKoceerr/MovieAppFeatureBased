package com.mustafakocer.feature_movies.details.data.repository

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_domain.util.mapSuccess
import com.mustafakocer.core_network.util.safeApiCall
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.data.mapper.toDomain
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailsRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService, // ← UPDATED: Single service
) : MovieDetailsRepository {

    override fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetails>> =
        // Artık tüm mantık, merkezi ve güvenli olan safeApiCall fonksiyonuna devredildi.
        safeApiCall {
            movieApiService.getMovieDetails(movieId)

        }.map { resource ->
            // safeApiCall'dan gelen Resource<MovieDetailsDto>'yu alıp,
            // onu Resource<MovieDetails>'e dönüştürüyoruz.
            // Manuel "when" bloğu yerine merkezi "map" fonksiyonu kullanıyoruz.
            resource.mapSuccess { movieDetailsDto ->
                movieDetailsDto.toDomain()
            }
        }

}