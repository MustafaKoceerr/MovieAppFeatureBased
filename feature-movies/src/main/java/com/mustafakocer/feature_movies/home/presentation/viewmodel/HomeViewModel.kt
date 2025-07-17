package com.mustafakocer.feature_movies.home.presentation.viewmodel

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_android.presentation.LoadingType
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.home.domain.usecase.GetMovieCategoryUseCase
import com.mustafakocer.feature_movies.home.presentation.contract.*
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMovieCategoryUseCase: GetMovieCategoryUseCase,
    private val languageRepository: LanguageRepository,
) : com.mustafakocer.core_android.presentation.BaseViewModel<HomeUiState, HomeEvent, HomeEffect>(HomeUiState()) {

    init {
        //  ViewModel oluşturulduğunda verileri otomatik olarak yükle.
        viewModelScope.launch {
            languageRepository.languageFlow
                .distinctUntilChanged() // Sadece dil gerçekten değiştiğinde tetikle
                .collect { newLanguage ->
                    // Log ekleyerek çalıştığını görebiliriz.
                    Log.d(
                        "HomeViewModelDebug",
                        "Dil değişti veya ilk kez okundu: $newLanguage. Veriler yeniden yükleniyor."
                    )
                    loadAllCategories(loadingType = com.mustafakocer.core_android.presentation.LoadingType.MAIN)
                }
        }


    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> loadAllCategories(loadingType = com.mustafakocer.core_android.presentation.LoadingType.REFRESH)
            is HomeEvent.MovieClicked -> com.mustafakocer.core_android.presentation.BaseViewModel.sendEffect(
                HomeEffect.NavigateToMovieDetails(event.movieId)
            )
            is HomeEvent.ViewAllClicked -> com.mustafakocer.core_android.presentation.BaseViewModel.sendEffect(
                HomeEffect.NavigateToMovieList(
                    event.category.apiEndpoint
                )
            )

            is HomeEvent.ProfileClicked -> com.mustafakocer.core_android.presentation.BaseViewModel.sendEffect(
                HomeEffect.NavigateToSettings
            )
            is HomeEvent.SearchClicked -> com.mustafakocer.core_android.presentation.BaseViewModel.sendEffect(
                HomeEffect.NavigateToSearch
            )
        }
    }

    private fun loadAllCategories(loadingType: com.mustafakocer.core_android.presentation.LoadingType) {
        com.mustafakocer.core_android.presentation.BaseViewModel.executeSafeOnce(loadingType) {
            val categoriesToFetch = MovieCategory.getAllCategories()

            categoriesToFetch.forEach { category ->
                viewModelScope.launch {
                    getMovieCategoryUseCase(category)
                        .collect { movies ->
                            Log.d(
                                "ViewModelDebug",
                                "[${category.name}] Yeni veri geldi. Boyut: ${movies.size}"
                            )
                            com.mustafakocer.core_android.presentation.BaseViewModel.setState {
                                copy(
                                    categories = com.mustafakocer.core_android.presentation.BaseViewModel.currentState.categories + (category to movies)
                                )
                            }
                        }
                }
            }
        }
    }

    override fun handleError(error: AppException): HomeUiState {
        return com.mustafakocer.core_android.presentation.BaseViewModel.currentState.copy(error = error)
    }

    override fun setLoading(loadingType: com.mustafakocer.core_android.presentation.LoadingType, isLoading: Boolean): HomeUiState {
        return when (loadingType) {
            com.mustafakocer.core_android.presentation.LoadingType.MAIN -> com.mustafakocer.core_android.presentation.BaseViewModel.currentState.copy(isLoading = isLoading)
            com.mustafakocer.core_android.presentation.LoadingType.REFRESH -> com.mustafakocer.core_android.presentation.BaseViewModel.currentState.copy(isRefreshing = isLoading)
        }
    }

}