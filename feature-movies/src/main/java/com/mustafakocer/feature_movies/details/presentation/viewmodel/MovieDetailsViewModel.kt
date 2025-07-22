package com.mustafakocer.feature_movies.details.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_android.presentation.LoadingType
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.feature_movies.details.domain.usecase.GetMovieDetailsUseCase
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsUiState
import com.mustafakocer.navigation_contracts.navigation.MovieDetailsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

    private var dataCollectionJob: Job? = null

    init {
        loadMovieDetails(loadingType = LoadingType.MAIN)
    }

    override fun onEvent(event: MovieDetailsEvent) {
        when (event) {
            is MovieDetailsEvent.Refresh -> loadMovieDetails(loadingType = LoadingType.REFRESH)
            is MovieDetailsEvent.ShareMovie -> handleShareMovie(event.content)
            is MovieDetailsEvent.BackPressed -> sendEffect(MovieDetailsEffect.NavigateBack)
            is MovieDetailsEvent.DismissError -> setState { copy(error = null) }
        }
    }

    private fun loadMovieDetails(loadingType: LoadingType) {
        // Önceki veri dinleme işlemini iptal et.
        dataCollectionJob?.cancel()

        // UseCase'den gelen reaktif akışı dinlemeye başla.
        dataCollectionJob = getMovieDetailsUseCase(movieId)
            .onEach { resource ->
                val newState = when (resource) {
                    is Resource.Loading -> {
                        when (loadingType) {
                            LoadingType.MAIN -> currentState.copy(isLoading = true, error = null)
                            LoadingType.REFRESH -> currentState.copy(
                                isRefreshing = true,
                                error = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            movie = resource.data
                        )
                    }

                    is Resource.Error -> {
                        currentState.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = resource.exception
                        )
                    }
                }
                setState { newState }
            }
            .launchIn(viewModelScope)
    }

    private fun handleShareMovie(shareContent: String) {
        setState { copy(isSharing = true) }
        sendEffect(MovieDetailsEffect.ShareContent(shareContent))
        // Paylaşım işlemi anlık olduğu için, state'i hemen geri alabiliriz.
        // Eğer uzun süren bir işlem olsaydı, bir callback mekanizması gerekirdi.
        setState { copy(isSharing = false) }
    }
}