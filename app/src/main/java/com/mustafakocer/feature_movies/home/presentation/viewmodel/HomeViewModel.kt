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

/**
 * Manages the UI state and business logic for the home screen.
 *
 * This ViewModel is responsible for orchestrating the fetching of home screen data,
 * handling user interactions, and emitting UI state updates and one-off side effects.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
) : BaseViewModel<HomeUiState, HomeEvent, HomeEffect>(
    HomeUiState()
) {
    // A reference to the current data collection job.
    // This is used to cancel any ongoing data fetch operations when a new one is initiated,
    // preventing race conditions and redundant network calls, especially on configuration changes
    // or rapid user interactions (e.g., pull-to-refresh).
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

    /**
     * Fetches the home screen data from the domain layer.
     *
     * It distinguishes between an initial load and a user-initiated refresh to provide
     * appropriate visual feedback (full-screen loader vs. swipe-to-refresh indicator).
     *
     * @param isRefresh Indicates if the data load was triggered by a pull-to-refresh action.
     */
    private fun loadData(isRefresh: Boolean) {
        // Architectural Decision: We immediately show the refresh animation for a better
        // user experience on pull-to-refresh, providing instant feedback.
        // The animation is stopped when the data stream emits a final state (Success or Error).
        if (isRefresh) {
            setState { copy(isRefreshing = true) }
        }

        dataCollectionJob?.cancel()
        dataCollectionJob = getHomeScreenDataUseCase(isRefresh = isRefresh)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // We only show the full-screen loading indicator on the initial app start
                        // when there is no data to display. For subsequent automatic refreshes
                        // or background updates, we don't want to block the UI.
                        if (uiState.value.categories.isEmpty()) {
                            setState { copy(isLoading = true, error = null) }
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
            .launchIn(viewModelScope)
    }
}