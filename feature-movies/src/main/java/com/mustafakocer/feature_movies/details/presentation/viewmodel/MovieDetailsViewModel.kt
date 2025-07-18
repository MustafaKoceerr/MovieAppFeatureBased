package com.mustafakocer.feature_movies.details.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_android.presentation.LoadingType
import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.feature_movies.details.domain.usecase.GetMovieDetailsUseCase
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsUiState
import com.mustafakocer.navigation_contracts.navigation.MovieDetailsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MovieDetailsUiState, MovieDetailsEvent, MovieDetailsEffect>(
    initialState = MovieDetailsUiState()
) {
    private val movieId: Int = savedStateHandle.get<Int>(MovieDetailsScreen.KEY_MOVIE_ID)
        ?: throw IllegalStateException("movieId is required")

    init {
        loadMovieDetails(loadingType = LoadingType.MAIN)
    }

    override fun onEvent(event: MovieDetailsEvent) {
        when (event) {
            is MovieDetailsEvent.Refresh -> loadMovieDetails(loadingType = LoadingType.REFRESH)
            is MovieDetailsEvent.ShareMovie -> handleShareMovie(event.content)
            is MovieDetailsEvent.BackPressed -> sendEffect(
                MovieDetailsEffect.NavigateBack
            )

            is MovieDetailsEvent.DismissError -> setState {
                copy(
                    error = null
                )
            }
        }
    }

    private fun loadMovieDetails(loadingType: LoadingType) {
        // executeSafeOnce yerine, Flow'u doğrudan dinliyoruz.
        getMovieDetailsUseCase(movieId)
            .onEach { resource ->
                // onEach, Flow'dan gelen her bir emit için bu bloğu çalıştırır.
                val newState = when (resource) {
                    is Resource.Loading -> {
                        // Yükleniyor durumunda, hangi tür yükleme olduğunu (ilk mi, yenileme mi)
                        // state'e yansıt.
                        when (loadingType) {
                            LoadingType.MAIN -> currentState.copy(isLoading = true)
                            LoadingType.REFRESH -> currentState.copy(isRefreshing = true)
                        }
                    }

                    is Resource.Success -> {
                        // Başarı durumunda, veriyi state'e yaz ve tüm yükleme/hata
                        // durumlarını temizle.
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            movie = resource.data
                        )
                    }

                    is Resource.Error -> {
                        // Hata durumunda, hatayı state'e yaz ve tüm yükleme
                        // durumlarını temizle.
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = resource.exception
                        )
                    }
                }
                // Hesaplanan yeni state'i tek bir yerden UI'a bildir.
                setState { newState }
            }
            .launchIn(viewModelScope) // Bu Flow'u viewModelScope'ta başlat ve dinle.
    }

    private fun handleShareMovie(shareContent: String) {
        setState { copy(isSharing = true) }

        sendEffect(MovieDetailsEffect.ShareContent(shareContent))

        setState { copy(isSharing = false) }
    }


}