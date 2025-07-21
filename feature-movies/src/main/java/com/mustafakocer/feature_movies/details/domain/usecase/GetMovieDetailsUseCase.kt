package com.mustafakocer.feature_movies.details.domain.usecase

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * Belirli bir filmin detaylarını, dil değişikliklerini dinleyerek getiren UseCase.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieDetailsRepository,
    private val languageRepository: LanguageRepository,
) {
    /**
     * Belirtilen film ID'si için detayları içeren bir Flow döndürür.
     * Bu akış, uygulama dili her değiştiğinde otomatik olarak en güncel veriyi yayınlar.
     *
     * @param movieId Detayları alınacak filmin ID'si.
     * @return Dil değişikliklerine duyarlı Resource<MovieDetails> akışı.
     */
    operator fun invoke(movieId: Int): Flow<Resource<MovieDetails>> {
        require(movieId > 0) { "Movie ID must be positive, but was: $movieId" }

        // 1. Dil akışını dinle.
        return languageRepository.languageFlow
            .flatMapLatest {
                // 2. Dil her değiştiğinde, repository'den o dile ait yeni detayları al.
                // Gerçek dil parametresi, ağ katmanındaki LanguageInterceptor tarafından eklenir.
                // UseCase'in sadece değişikliği tetiklemesi yeterlidir.
                repository.getMovieDetails(movieId)
            }
    }
}

