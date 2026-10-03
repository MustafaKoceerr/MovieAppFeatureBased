package com.mustafakocer.movieappfeaturebasedclean.feature.details.data.repository

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_domain.util.mapSuccess
import com.mustafakocer.core_network.util.safeApiCall
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.api.MovieApiService
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.mapper.toDomain
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieDetails
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Fetches movie details from the API. The result is re-fetched whenever the app language changes,
 * because the API returns localized titles and overviews.
 */
@Singleton
class MovieDetailsRepository @Inject constructor(
    private val movieApiService: MovieApiService,
    private val languageRepository: LanguageRepository,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetails>> =
        languageRepository.languageFlow.flatMapLatest {
            safeApiCall { movieApiService.getMovieDetails(movieId) }
                .map { resource -> resource.mapSuccess { dto -> dto.toDomain() } }
        }
}
