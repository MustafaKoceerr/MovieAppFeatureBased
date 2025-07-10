package com.mustafakocer.feature_movies.list.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.feature_movies.home.presentation.contract.MovieDetailRoute
import com.mustafakocer.feature_movies.list.domain.model.MovieCategory
import com.mustafakocer.feature_movies.list.domain.usecase.GetMovieListUseCase
import com.mustafakocer.feature_movies.list.domain.usecase.RefreshMovieListUseCase
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEffect
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEvent
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Movie List ViewModel
 *
 * Clean architecture: Presentation Layer- State Management
 * Responsibility: Manage movie list UI state and user interactions
 *
 * PAGING3 INTEGRATION:
 * - cachedIn(viewModelScope) for configuration changes
 * - Flow<PagingData> for reactive pagination
 * - Automatic loading state management via Paging3
 */

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getMovieListUseCase: GetMovieListUseCase,
    private val refreshMovieListUseCase: RefreshMovieListUseCase,
) : ViewModel(), UiContract<MovieListUiState, MovieListEvent, MovieListEffect> {
    // UiContract implementation
    private val _uiState = MutableStateFlow(MovieListUiState())
    override val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MovieListEffect>()
    override val uiEffect: SharedFlow<MovieListEffect> = _uiEffect.asSharedFlow()

    private var currentCategory: MovieCategory? = null

    override fun onEvent(event: MovieListEvent) {
        when (event) {
            is MovieListEvent.InitializeScreen -> initializeScreen(
                event.categoryEndpoint,
                event.categoryTitle
            )

            is MovieListEvent.MovieClicked -> navigateToMovieDetail(event.movieId)
            is MovieListEvent.NavigateBackClicked -> navigateBack()
        }
    }

    // ==================== PUBLIC NAVIGATION METHODS ====================
    /**
     * Navigate to movie detail screen
     */
    fun navigateToMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _uiEffect.emit(
                MovieListEffect.NavigateToMovieDetail(
                    MovieDetailRoute(movieId = movieId)
                )
            )
        }
    }

    /**
     * Navigate back to previous screen
     */
    fun navigateBack() {
        viewModelScope.launch {
            _uiEffect.emit(MovieListEffect.NavigateBack)
        }
    }

    // ==================== PRIVATE EVENT HANDLERS ====================

    /**
     * Initialize screen with category parameters
     */
    private fun initializeScreen(categoryEndpoint: String, categoryTitle: String) {
        val category = MovieCategory.fromApiEndpoint(categoryEndpoint)

        if (category == null) {
            viewModelScope.launch {
                _uiEffect.emit(MovieListEffect.ShowError("Invalid category: $categoryEndpoint"))
            }
            return
        }

        currentCategory = category
        _uiState.value = _uiState.value.copy(
            categoryTitle = categoryTitle,
            categoryEndpoint = categoryEndpoint,
            isLoading = false
        )
        loadMovies(category)
    }

    /**
     * Load movies for current category
     */
    private fun loadMovies(category: MovieCategory) {
        val moviesPagingFlow = getMovieListUseCase(category)
            .cachedIn(viewModelScope) // Important for config changes

        _uiState.value = _uiState.value.copy(
            movies = moviesPagingFlow,
            isLoading = false,
            error = null
        )
    }

    /**
     * Retry loading data after error
     */
    private fun retryLoadData() {
        val category = currentCategory ?: return
        _uiState.value = _uiState.value.copy(error = null)
        loadMovies(category)
    }
}