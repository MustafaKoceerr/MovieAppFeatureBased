package com.mustafakocer.feature_movies.home.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_android.presentation.LoadingType
import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.home.domain.usecase.GetMovieCategoryUseCase
import com.mustafakocer.feature_movies.home.presentation.contract.*
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMovieCategoryUseCase: GetMovieCategoryUseCase,
    private val languageRepository: LanguageRepository,
) : BaseViewModel<HomeUiState, HomeEvent, HomeEffect>(
    HomeUiState()
) {

    init {
        viewModelScope.launch {
            languageRepository.languageFlow
                .distinctUntilChanged()
                .collect { newLanguage ->
                    Log.d(
                        "HomeViewModelDebug",
                        "Dil değişti: $newLanguage. Veri yükleme tetikleniyor."
                    )
                    loadAllCategories(loadingType = LoadingType.MAIN)
                }
        }
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> loadAllCategories(loadingType = LoadingType.REFRESH)
            is HomeEvent.MovieClicked -> sendEffect(HomeEffect.NavigateToMovieDetails(event.movieId))
            is HomeEvent.ViewAllClicked -> sendEffect(HomeEffect.NavigateToMovieList(event.category.apiEndpoint))
            is HomeEvent.ProfileClicked -> sendEffect(HomeEffect.NavigateToSettings)
            is HomeEvent.SearchClicked -> sendEffect(HomeEffect.NavigateToSearch)
        }
    }

    private fun loadAllCategories(loadingType: LoadingType) {
        // Yükleme durumunu başlat ve önceki hataları temizle.
        setState {
            when (loadingType) {
                LoadingType.MAIN -> copy(isLoading = true, error = null)
                LoadingType.REFRESH -> copy(isRefreshing = true, error = null)
            }
        }
        val categoriesToFetch = MovieCategory.getAllCategories()

        viewModelScope.launch(SupervisorJob()) {
            // 1. her kategori için paralel bir 'async' görevi başlat.
            // Bu, her bir görevin sonucunu (Resource) tutan bir "söz" (Deferred) döndürür.
            val deferredResults = categoriesToFetch.map { category ->
                async {
                    getMovieCategoryUseCase(category).toList()
                }
            }

            val resultsList = deferredResults.awaitAll()

            val successfulCategories = categoriesToFetch.zip(resultsList)
                .mapNotNull { (category, resources) ->
                    // Bu listedeki, 'Success' olan SON değeri bul.
                    // Bu, ağdan gelen en güncel veridir.
                    val lastSuccessResource =
                        resources.filterIsInstance<Resource.Success<List<MovieListItem>>>()
                            .lastOrNull()

                    if (lastSuccessResource != null && lastSuccessResource.data.isNotEmpty()) {
                        category to lastSuccessResource.data
                    } else {
                        // Eğer hiç başarılı sonuç yoksa, bir hata olup olmadığını kontrol et.
                        val errorResource =
                            resources.filterIsInstance<Resource.Error>().firstOrNull()
                        if (errorResource != null) {
                            Log.e(
                                "HomeViewModelDebug",
                                "Kategori yüklenemedi: ${category.name}",
                                errorResource.exception
                            )
                        }
                        null
                    }
                }
                .toMap()

            setState {
                copy(
                    isLoading = false,
                    isRefreshing = false,
                    categories = successfulCategories,
                    error = null
                )
            }
        }
    }
}
