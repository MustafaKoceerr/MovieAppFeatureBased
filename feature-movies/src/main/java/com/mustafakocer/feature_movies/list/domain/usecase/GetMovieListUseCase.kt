package com.mustafakocer.feature_movies.list.domain.usecase

import androidx.paging.PagingData
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetMovieListUseCase @Inject constructor(
    private val repository: MovieListRepository,
    private val languageRepository: LanguageRepository,
) {
    /**
     * Belirtilen kategori için PagingData akışını döndürür.
     * Bu akış, dil her değiştiğinde otomatik olarak güncellenir.
     *
     * @param category Film listesinin alınacağı kategori.
     * @return Dil değişikliklerine duyarlı PagingData akışı.
     */
    operator fun invoke(category: MovieCategory): Flow<PagingData<MovieListItem>> {
        // 1. dil akışını dinle.
        return languageRepository.languageFlow
            .flatMapLatest { language ->
                // 2. dil her değiştiğinde, repository'den o dile ait paging data akışını al.
                // flatmapLatest, önceki dil için olan akışı otomatik olarak iptal eder.
                repository.getMoviesByCategory(
                    category = category,
                    language = language.apiParam
                )
            }
    }
}