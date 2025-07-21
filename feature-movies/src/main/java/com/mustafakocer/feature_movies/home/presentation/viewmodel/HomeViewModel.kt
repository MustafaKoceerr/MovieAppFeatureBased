package com.mustafakocer.feature_movies.home.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_android.presentation.LoadingType
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.feature_movies.home.domain.usecase.GetHomeScreenDataUseCase
import com.mustafakocer.feature_movies.home.presentation.contract.HomeEffect
import com.mustafakocer.feature_movies.home.presentation.contract.HomeEvent
import com.mustafakocer.feature_movies.home.presentation.contract.HomeUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
) : BaseViewModel<HomeUiState, HomeEvent, HomeEffect>(
    HomeUiState()
) {
    private var dataCollectionJob: Job? = null

    init {
        loadData(LoadingType.MAIN)
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> loadData(LoadingType.REFRESH)
            is HomeEvent.MovieClicked -> sendEffect(HomeEffect.NavigateToMovieDetails(event.movieId))
            is HomeEvent.ViewAllClicked -> sendEffect(HomeEffect.NavigateToMovieList(event.category.apiEndpoint))
            is HomeEvent.SettingsClicked -> sendEffect(HomeEffect.NavigateToSettings)
            is HomeEvent.AccountClicked -> sendEffect(HomeEffect.NavigateToAccount)
            is HomeEvent.SearchClicked -> sendEffect(HomeEffect.NavigateToSearch)

        }
    }

    /**
     * Veri toplama işini başlatan fonksiyon.
     * Önceki işi iptal eder ve UseCase'den gelen akışı dinlemeye başlar.
     */
    private fun loadData(loadingType: LoadingType) {
        dataCollectionJob?.cancel()
        dataCollectionJob = viewModelScope.launch {
            getHomeScreenDataUseCase().collect { resource ->
                updateState(resource, loadingType)
            }
        }
    }

    /**
     * Gelen `Resource`'a göre UI durumunu güncelleyen yardımcı fonksiyon.
     * Bu, `loadData` fonksiyonunu temiz tutar ve state mantığını merkezileştirir.
     */
    private fun updateState(
        resource: Resource<Map<MovieCategory, List<MovieListItem>>>,
        loadingType: LoadingType,
    ) {
        when (resource) {
            is Resource.Loading -> {
                setState {
                    // Sadece önbellek boşken tam ekran yükleme göster.
                    if (categories.isEmpty()) {
                        copy(isLoading = true, error = null)
                    } else {
                        // Veri zaten varken sadece yenileme göstergesini aktifleştir.
                        copy(isRefreshing = (loadingType == LoadingType.REFRESH))
                    }
                }
            }

            is Resource.Success -> {
                setState {
                    copy(
                        isLoading = false,
                        isRefreshing = false,
                        categories = resource.data,
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
        }
    }
}