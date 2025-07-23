package com.mustafakocer.feature_movies.home.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.feature_movies.home.domain.usecase.GetHomeScreenDataUseCase
import com.mustafakocer.feature_movies.home.presentation.contract.HomeEffect
import com.mustafakocer.feature_movies.home.presentation.contract.HomeEvent
import com.mustafakocer.feature_movies.home.presentation.contract.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
) : BaseViewModel<HomeUiState, HomeEvent, HomeEffect>(
    HomeUiState()
) {
    private var dataCollectionJob: Job? = null

    init {
        loadData(isRefresh = false)
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> loadData(isRefresh = true)
            is HomeEvent.MovieClicked -> sendEffect(HomeEffect.NavigateToMovieDetails(event.movieId))
            is HomeEvent.ViewAllClicked -> sendEffect(HomeEffect.NavigateToMovieList(event.category.apiEndpoint))
            is HomeEvent.SettingsClicked -> sendEffect(HomeEffect.NavigateToSettings)
            is HomeEvent.AccountClicked -> sendEffect(HomeEffect.NavigateToAccount)
            is HomeEvent.SearchClicked -> sendEffect(HomeEffect.NavigateToSearch)
        }
    }

    private fun loadData(isRefresh: Boolean) {
        // Eğer bu bir "pull-to-refresh" ise, animasyonu hemen başlat.
        if (isRefresh) {
            setState { copy(isRefreshing = true) }
        }

        dataCollectionJob?.cancel()
        dataCollectionJob = getHomeScreenDataUseCase(isRefresh = isRefresh)
            .onEach { resource ->
                // Sadece nihai sonuçlar (Success veya Error) geldiğinde
                // veya ilk yükleme (isLoading) sırasında state'i güncelle.
                when (resource) {
                    is Resource.Loading -> {
                        // Sadece ilk yükleme ise (ekranda hiç kategori yoksa)
                        // tam ekran yükleme göstergesini aç.
                        if (uiState.value.categories.isEmpty()) {
                            setState { copy(isLoading = true, error = null) }
                        }
                    }

                    is Resource.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                isRefreshing = false, // <-- Animasyonu burada bitir
                                categories = resource.data,
                                error = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                isRefreshing = false, // <-- Animasyonu burada bitir
                                error = resource.exception
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

}