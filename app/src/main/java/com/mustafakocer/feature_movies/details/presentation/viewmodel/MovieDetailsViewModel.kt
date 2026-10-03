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
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Manages the business logic for the Movie Details screen.
 *
 * @param getMovieDetailsUseCase The use case for fetching detailed movie information.
 * @param savedStateHandle A handle to the saved state, used to retrieve navigation arguments.
 *
 * Architectural Note:
 * This ViewModel is responsible for fetching and displaying the details of a single movie.
 * - **Argument Retrieval:** It safely retrieves the `movieId` from `SavedStateHandle`, ensuring it's
 *   decoupled from the navigation framework's specifics.
 * - **Lifecycle-Aware Data Fetching:** It uses a single `dataCollectionJob` to manage the subscription
 *   to the data flow. This job is cancelled and restarted on each new data request (e.g., on refresh),
 *   preventing multiple active collectors and resource leaks.
 * - **Differentiated Loading States:** The `loadMovieDetails` function uses a `LoadingType` enum to
 *   update the UI state appropriately for either an initial full-screen load or a less intrusive
 *   pull-to-refresh indicator.
 */
@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MovieDetailsUiState, MovieDetailsEvent, MovieDetailsEffect>(
    initialState = MovieDetailsUiState()
) {
    private val movieId: Int = savedStateHandle.get<Int>(MovieDetailsScreen.KEY_MOVIE_ID)
        ?: throw IllegalStateException("movieId is required for MovieDetailsViewModel")

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
        // Architectural Decision:
        // The existing `dataCollectionJob` is cancelled before starting a new one. This is a crucial
        // pattern to prevent multiple, concurrent data streams if the user triggers a refresh while
        // a previous request is still in flight.
        dataCollectionJob?.cancel()

        dataCollectionJob = getMovieDetailsUseCase(movieId)
            .onEach { resource ->
                val newState = when (resource) {
                    is Resource.Loading -> {
                        when (loadingType) {
                            LoadingType.MAIN -> currentState.copy(isLoading = true, error = null)
                            LoadingType.REFRESH -> currentState.copy(isRefreshing = true, error = null)
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
        // The sharing state is toggled immediately. Since the share action is a fire-and-forget
        // intent, we don't need a complex callback mechanism to reset the state.
        setState { copy(isSharing = false) }
    }
}