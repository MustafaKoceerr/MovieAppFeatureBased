package com.mustafakocer.feature_movies.details.data.repository

import com.mustafakocer.core_common.util.Resource
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
            when (resource) {
                is Resource.Success -> {
                    // Başarı durumunda, içindeki DTO'yu domain modeline çevir.
                    val domainModel = resource.data.toDomain()
                    Resource.Success(domainModel)
                }
                // Hata ve Yükleniyor durumlarında, içlerinde veri olmadığı için
                // olduğu gibi geri döndür.
                is Resource.Error -> resource
                is Resource.Loading -> resource
            }
        }

}