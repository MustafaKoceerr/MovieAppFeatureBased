package com.mustafakocer.feature_movies.home.presentation.viewmodel

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseViewModel
import com.mustafakocer.core_common.presentation.LoadingType
import com.mustafakocer.feature_movies.home.domain.usecase.GetMovieCategoryUseCase
import com.mustafakocer.feature_movies.home.presentation.contract.*
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMovieCategoryUseCase: GetMovieCategoryUseCase,
) : BaseViewModel<HomeUiState, HomeEvent, HomeEffect>(HomeUiState()) {

    init {
        //  ViewModel oluşturulduğunda verileri otomatik olarak yükle.
        loadAllCategories(loadingType = LoadingType.MAIN)
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> loadAllCategories(loadingType = LoadingType.REFRESH)
            is HomeEvent.MovieClicked -> sendEffect(HomeEffect.NavigateToMovieDetails(event.movieId))
            is HomeEvent.ViewAllClicked -> sendEffect(
                HomeEffect.NavigateToMovieList(
                    event.category.title,
                    event.category.apiEndpoint
                )
            )

            is HomeEvent.ProfileClicked -> sendEffect(HomeEffect.NavigateToSettings)
            is HomeEvent.SearchClicked -> sendEffect(HomeEffect.NavigateToSearch)
        }
    }

    private fun loadAllCategories(loadingType: LoadingType) {
        executeSafeOnce(loadingType) {
            val categoriesToFetch = MovieCategory.getAllCategories()

            val categoriesMap = coroutineScope {
                // Kategorileri enum'dan alıp, her biri için paralel bir görev başlatıyoruz.
                val deferredResults = categoriesToFetch.map { category ->
                    category to async { getMovieCategoryUseCase(category).first() }
                }
                // Sonuçları bekleyip, (Kategori, FilmListesi) çiftlerinden bir Map oluşturuyoruz.
                deferredResults.associate { (category, deferred) ->
                    category to deferred.await()
                }
            }

            setState {
                copy(
                    categories = categoriesMap,
                    error = null
                )
            }
        }
    }

    override fun handleError(error: AppException): HomeUiState {
        return currentState.copy(error = error)
    }

    override fun setLoading(loadingType: LoadingType, isLoading: Boolean): HomeUiState {
        return when (loadingType) {
            LoadingType.MAIN -> currentState.copy(isLoading = isLoading)
            LoadingType.REFRESH -> currentState.copy(isRefreshing = isLoading)
        }
    }

}