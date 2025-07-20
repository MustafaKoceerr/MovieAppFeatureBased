package com.mustafakocer.feature_movies.search.domain.usecase

import androidx.paging.PagingData
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.search.domain.model.SearchQuery
import com.mustafakocer.feature_movies.search.domain.repository.SearchRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Arama sorgusunu ve dil değişikliklerini dinleyerek film aramasını yöneten UseCase.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchMoviesUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val languageRepository: LanguageRepository,
) {
    /**
     * Arama sorgusu akışını ve dil akışını birleştirerek PagingData akışını döndürür.
     *
     * @param queryFlow ViewModel'den gelen, kullanıcının girdiği arama sorgularını içeren akış.
     * @return Arama sorgusuna ve dil değişikliklerine duyarlı PagingData akışı.
     */
    operator fun invoke(queryFlow: Flow<String>): Flow<PagingData<MovieListItem>> {
        // Dil akışını ve sorgu akışını birleştir.
        val combinedFlow = combine(queryFlow, languageRepository.languageFlow) { query, _ ->
            query // Sadece sorguyu al, dil değişikliği sadece tetikleyici.
        }

        return combinedFlow.flatMapLatest { query ->
            val searchQuery = SearchQuery.create(query)

            // Business Rule: Sorgu geçerli değilse (örn. 2 karakterden kısa),
            // boş bir PagingData akışı döndür. Bu, ViewModel'deki if/else'i ortadan kaldırır.
            if (!searchQuery.isValid) {
                flowOf(PagingData.empty())
            } else {
                searchRepository.searchMovies(searchQuery)
            }
        }
    }
}