package com.mustafakocer.feature_movies.home.domain.usecase

import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.home.domain.repository.HomeRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * Ana ekran verilerini (tüm kategoriler) yöneten orkestratör UseCase.
 *
 * Sorumlulukları:
 * 1. Dil değişikliklerini dinler.
 * 2. Dil değiştiğinde, tüm film kategorilerini paralel olarak getirir.
 * 3. Sonuçları tek bir merkezi `Resource` akışında birleştirir.
 * Bu, iş mantığını (policy) ViewModel'den alıp Domain katmanına taşır.
 */

/**
 * Ana ekran verilerini (tüm kategoriler) yöneten orkestratör UseCase.
 *
 * Sorumlulukları:
 * 1. Dil değişikliklerini dinler.
 * 2. Dil değiştiğinde, tüm film kategorilerini paralel olarak getirir.
 * 3. Sonuçları tek bir merkezi `Resource` akışında birleştirir.
 * Bu, iş mantığını (policy) ViewModel'den alıp Domain katmanına taşır.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetHomeScreenDataUseCase @Inject constructor(
    private val homeRepository: HomeRepository, // Artık tek bir repository'ye bağımlı
    private val languageRepository: LanguageRepository,
) {
    operator fun invoke(): Flow<Resource<Map<MovieCategory, List<MovieListItem>>>> {
        // 1. Dil akışını dinle. Dil değiştiğinde, flatMapLatest içindeki blok yeniden tetiklenir.
        return languageRepository.languageFlow.flatMapLatest {
            // 2. Tüm kategorileri paralel olarak getirmek için bir akış oluştur.
            fetchAllCategories()
        }
    }

    private fun fetchAllCategories(): Flow<Resource<Map<MovieCategory, List<MovieListItem>>>> {
        val categories = MovieCategory.getAllCategories()

        // 3. Her bir kategori için repository'yi çağıran Flow'ların bir listesini oluştur.
        val categoryFlows: List<Flow<Resource<List<MovieListItem>>>> = categories.map { category ->
            homeRepository.getMoviesForCategory(category)
        }

        // 4. `combine` operatörü ile tüm bu akışları birleştir.
        return combine(categoryFlows) { resources ->
            val resultMap = mutableMapOf<MovieCategory, List<MovieListItem>>()
            var firstError: Resource.Error? = null

            // 5. Gelen tüm sonuçları (her kategori için bir Resource) işle.
            resources.forEachIndexed { index, resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (resource.data.isNotEmpty()) {
                            resultMap[categories[index]] = resource.data
                        }
                    }

                    is Resource.Error -> {
                        if (firstError == null) firstError = resource
                    }

                    is Resource.Loading -> {
                        // Loading durumları burada genellikle göz ardı edilir,
                        // çünkü genel bir Loading durumu onStart ile yönetilir.
                    }
                }
            }

            // 6. Sonucu belirle.
            val result: Resource<Map<MovieCategory, List<MovieListItem>>> =
                if (resultMap.isNotEmpty()) {
                    Resource.Success(resultMap)
                } else if (firstError != null) {
                    firstError!!
                } else {
                    Resource.Success(emptyMap())
                }
            result
        }.onStart {
            // 7. Akış başladığında, ilk olarak bir `Loading` durumu yay.
            // Bu, `flow { emit(Loading); emitAll(...) }` yapısından daha şık bir yoldur.
            emit(Resource.Loading)
        }
    }
}