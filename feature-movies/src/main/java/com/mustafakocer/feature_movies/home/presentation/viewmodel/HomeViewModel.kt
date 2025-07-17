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
import com.mustafakocer.core_android.presentation.BaseViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMovieCategoryUseCase: GetMovieCategoryUseCase,
    private val languageRepository: LanguageRepository,
) : BaseViewModel<HomeUiState, HomeEvent, HomeEffect>(
    HomeUiState()
) {

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
                    loadAllCategories(loadingType = LoadingType.MAIN)
                }
        }


    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> loadAllCategories(loadingType = LoadingType.REFRESH)
            is HomeEvent.MovieClicked -> sendEffect(
                HomeEffect.NavigateToMovieDetails(event.movieId)
            )

            is HomeEvent.ViewAllClicked -> sendEffect(
                HomeEffect.NavigateToMovieList(
                    event.category.apiEndpoint
                )
            )

            is HomeEvent.ProfileClicked -> sendEffect(
                HomeEffect.NavigateToSettings
            )

            is HomeEvent.SearchClicked -> sendEffect(
                HomeEffect.NavigateToSearch
            )
        }
    }

    private fun loadAllCategories(loadingType: LoadingType) {
        executeSafeOnce(loadingType) {
            val categoriesToFetch = MovieCategory.getAllCategories()

            categoriesToFetch.forEach { category ->
                viewModelScope.launch {
                    getMovieCategoryUseCase(category)
                        .collect { movies ->
                            Log.d(
                                "ViewModelDebug",
                                "[${category.name}] Yeni veri geldi. Boyut: ${movies.size}"
                            )
                            setState {
                                copy(
                                    categories = currentState.categories + (category to movies)
                                )
                            }
                        }
                }
            }
        }
    }

    override fun handleError(error: AppException): HomeUiState {
        return currentState.copy(error = error)
    }

    override fun setLoading(
        loadingType: LoadingType,
        isLoading: Boolean,
    ): HomeUiState {
        return when (loadingType) {
            LoadingType.MAIN -> currentState.copy(isLoading = isLoading)
            LoadingType.REFRESH -> currentState.copy(isRefreshing = isLoading)
        }
    }

}