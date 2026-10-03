package com.mustafakocer.feature_movies.list.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.feature_movies.list.domain.usecase.GetMovieListUseCase
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEffect
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEvent
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.navigation_contracts.navigation.MovieListScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Manages the UI state and business logic for the paginated movie list screen.
 *
 * Its responsibilities include:
 * - Retrieving the required movie category from navigation arguments via [SavedStateHandle].
 * - Invoking the [GetMovieListUseCase] to get a reactive stream of paginated movie data.
 * - Caching the PagingData stream within the ViewModel's lifecycle to handle configuration changes.
 * - Translating UI events into navigation or other side effects.
 */
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getMovieListUseCase: GetMovieListUseCase,
    // Architectural Decision: SavedStateHandle is used to access navigation arguments.
    // This is the recommended approach as it's lifecycle-aware and allows the ViewModel
    // to survive process death and configuration changes without losing its context.
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MovieListUiState, MovieListEvent, MovieListEffect>(
    initialState = MovieListUiState()
) {
    // The category endpoint is retrieved from the navigation arguments.
    private val categoryEndpoint: String = savedStateHandle[MovieListScreen.KEY_CATEGORY_ENDPOINT]
        ?: throw IllegalStateException("Category endpoint is required for MovieListScreen")

    init {
        val category = MovieCategory.fromApiEndpoint(categoryEndpoint)

        if (category == null) {
            sendEffect(MovieListEffect.ShowSnackbar("Category not found."))
            sendEffect(MovieListEffect.NavigateBack)
        } else {
            loadMovies(category)
        }
    }

    /**
     * Handles incoming user events from the UI.
     */
    override fun onEvent(event: MovieListEvent) {
        when (event) {
            is MovieListEvent.MovieClicked -> {
                sendEffect(MovieListEffect.NavigateToMovieDetail(event.movieId))
            }

            is MovieListEvent.BackClicked -> {
                sendEffect(MovieListEffect.NavigateBack)
            }
        }
    }

    /**
     * Initiates the loading of the paginated movie list for a given category.
     *
     * @param category The [MovieCategory] for which to load movies.
     */
    private fun loadMovies(category: MovieCategory) {

        val moviesPagingFlow = getMovieListUseCase(category)
            .cachedIn(viewModelScope)

        setState {
            copy(
                movies = moviesPagingFlow,
                category = category
            )
        }
    }
}