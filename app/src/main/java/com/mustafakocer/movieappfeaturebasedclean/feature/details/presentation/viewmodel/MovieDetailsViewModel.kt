package com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.movieappfeaturebasedclean.feature.details.data.repository.MovieDetailsRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieDetails
import com.mustafakocer.movieappfeaturebasedclean.navigation.MovieDetailsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class MovieDetailsUiState(
    val movie: MovieDetails? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: AppException? = null,
)

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val movieDetailsRepository: MovieDetailsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle[MovieDetailsScreen::movieId.name]) { "movieId is required" }

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    private var dataCollectionJob: Job? = null

    init {
        loadMovieDetails(isRefresh = false)
    }

    fun onRefresh() = loadMovieDetails(isRefresh = true)

    private fun loadMovieDetails(isRefresh: Boolean) {
        dataCollectionJob?.cancel()
        dataCollectionJob = movieDetailsRepository.getMovieDetails(movieId)
            .onEach { resource ->
                _uiState.update { current ->
                    when (resource) {
                        is Resource.Loading ->
                            if (isRefresh) current.copy(isRefreshing = true, error = null)
                            else current.copy(isLoading = true, error = null)

                        is Resource.Success -> current.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            movie = resource.data,
                        )

                        is Resource.Error -> current.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = resource.exception,
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
