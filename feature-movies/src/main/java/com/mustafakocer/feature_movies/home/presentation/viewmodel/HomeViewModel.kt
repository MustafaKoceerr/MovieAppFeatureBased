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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
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

        // Sadece ilk yükleme (MAIN) ve cache boşsa tam ekran spinner göster.
        if (loadingType == LoadingType.MAIN && currentState.categories.isEmpty()) {
            setState { copy(isLoading = true, error = null) }
        } else if (loadingType == LoadingType.REFRESH) {
            setState { copy(isRefreshing = true, error = null) }
        }
        val categoriesToFetch = MovieCategory.getAllCategories()

        viewModelScope.launch(SupervisorJob()) {
            categoriesToFetch.forEach { category ->
                // Her kategori için ayrı bir "dinleyici" coroutine'i başlat.
                launch {
                    getMovieCategoryUseCase(category)
                        .collect { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    setState {
                                        copy(
                                            isLoading = false,
                                            isRefreshing = false,
                                            categories = currentState.categories + (category to resource.data),
                                            error = null
                                        )
                                    }
                                }

                                is Resource.Error -> {
                                    setState {
                                        copy(
                                            isLoading = false,
                                            isRefreshing = false,
                                            error = resource.exception
                                        )
                                    }
                                }

                                is Resource.Loading -> {
                                    // Şimdilik, Success/Error gelene kadar beklemek daha temiz.
                                }
                            }
                        }
                }
            }
        }
    }

}