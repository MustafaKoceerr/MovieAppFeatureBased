package com.mustafakocer.feature_movies.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.result.onError
import com.mustafakocer.core_common.result.onSuccess
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.core_ui.component.error.ErrorInfo
import com.mustafakocer.core_ui.component.error.GeneralErrorType
import com.mustafakocer.core_ui.component.error.GenericErrorMessageFactory
import com.mustafakocer.core_ui.component.error.toGeneralErrorTypeOrNull
import com.mustafakocer.feature_movies.home.domain.model.HomeContent
import com.mustafakocer.feature_movies.home.domain.model.Movie
import com.mustafakocer.feature_movies.home.domain.model.MovieCategoryType
import com.mustafakocer.feature_movies.home.domain.usecase.GetMovieCategoryUseCase
import com.mustafakocer.feature_movies.home.presentation.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMovieCategoryUseCase: GetMovieCategoryUseCase,
) : ViewModel(), UiContract<HomeUiState, HomeEvent, HomeEffect> {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    override val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<HomeEffect>()
    override val uiEffect: SharedFlow<HomeEffect> = _uiEffect.asSharedFlow()

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadInitialData -> loadAllCategories()
            is HomeEvent.RefreshAllCategories -> refreshAllCategories()
            is HomeEvent.RetryClicked -> retryAllCategories()
            is HomeEvent.RetryCategory -> retryCategory(event.categoryType)
        }
    }

    init {
        onEvent(HomeEvent.LoadInitialData)
    }

    // ==================== NAVIGATION ====================

    fun navigateToMovieDetail(movieId: Int, movieTitle: String?) {
        viewModelScope.launch {
            _uiEffect.emit(
                HomeEffect.NavigateToMovieDetail(
                    MovieDetailRoute(movieId = movieId, movieTitle = movieTitle)
                )
            )
        }
    }

    fun navigateToMoviesList(categoryType: MovieCategoryType) {
        viewModelScope.launch {
            _uiEffect.emit(
                HomeEffect.NavigateToMoviesList(
                    MoviesListRoute(
                        category = categoryType.apiEndpoint,
                        title = categoryType.displayName
                    )
                )
            )
        }
    }

    fun navigateToSearch() {
        viewModelScope.launch {
            _uiEffect.emit(HomeEffect.NavigateToSearch)
        }
    }

    fun navigateToProfile() {
        viewModelScope.launch {
            _uiEffect.emit(HomeEffect.NavigateToProfile)
        }
    }

    // ==================== PRIVATE METHODS ====================

    private fun loadAllCategories() {
        _uiState.value = HomeUiState.Loading

        // Load each category independently
        loadCategory(MovieCategoryType.NOW_PLAYING)
        loadCategory(MovieCategoryType.POPULAR)
        loadCategory(MovieCategoryType.TOP_RATED)
        loadCategory(MovieCategoryType.UPCOMING)
    }

    private fun loadCategory(categoryType: MovieCategoryType) {
        viewModelScope.launch {
            getMovieCategoryUseCase(categoryType, page = 1).collect { uiState ->
                uiState.onSuccess { movies ->
                    updateCategorySuccess(categoryType, movies)
                }.onError { exception ->
                    updateCategoryError(exception)
                }
            }
        }
    }

    private fun retryCategory(categoryType: MovieCategoryType) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(
                retryingCategories = currentState.retryingCategories + categoryType
            )
        }

        viewModelScope.launch {
            getMovieCategoryUseCase(categoryType, page = 1).collect { uiState ->
                uiState.onSuccess { movies ->
                    updateCategorySuccess(categoryType, movies)
                    removeFromRetrying(categoryType)
                }.onError { exception ->
                    removeFromRetrying(categoryType)
                    handleCategorySpecificError(categoryType, exception)
                }
            }
        }
    }

    private fun retryAllCategories() {
        loadAllCategories()
    }

    private fun refreshAllCategories() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(isRefreshing = true)

            viewModelScope.launch {
                loadAllCategories()
                // Remove refreshing state after load
                val newState = _uiState.value
                if (newState is HomeUiState.Success) {
                    _uiState.value = newState.copy(isRefreshing = false)
                }
                showSuccessMessage("Categories refreshed")
            }
        }
    }

    // ==================== ERROR HANDLING (HYBRID APPROACH) ====================

    /**
     * ✅ HYBRID APPROACH: Handle initial load errors (full screen)
     */
    private fun updateCategoryError(exception: AppException) {
        val currentState = _uiState.value

        if (currentState is HomeUiState.Loading) {
            // Show full screen error during initial load
            val errorInfo = createErrorInfoFromException(exception)
            _uiState.value = HomeUiState.Error(exception, errorInfo)
        } else {
            // Show toast for partial errors when content exists
            showErrorMessage(exception.userMessage)
        }
    }

    /**
     * ✅ HYBRID APPROACH: Handle category-specific retry errors
     */
    private fun handleCategorySpecificError(
        categoryType: MovieCategoryType,
        exception: AppException,
    ) {
        val errorMessage = when (exception) {
            // Contextual: Category-specific 404
            is AppException.ApiException.NotFound -> {
                "No ${categoryType.displayName.lowercase()} movies found"
            }
            // Global: Use general error message
            else -> {
                exception.toGeneralErrorTypeOrNull()?.let { generalType ->
                    when (generalType) {
                        GeneralErrorType.NO_INTERNET ->
                            "Check internet connection to load ${categoryType.displayName}"

                        GeneralErrorType.SERVER_ERROR ->
                            "Server error loading ${categoryType.displayName}"

                        else -> "Failed to load ${categoryType.displayName}"
                    }
                } ?: "Failed to load ${categoryType.displayName}"
            }
        }
        showErrorMessage(errorMessage)
    }

    /**
     * ✅ HYBRID APPROACH: Create ErrorInfo using hybrid strategy
     */
    private fun createErrorInfoFromException(exception: AppException): ErrorInfo {
        return when (exception) {
            // Contextual: Feature-specific errors
            is AppException.ApiException.NotFound -> {
                GenericErrorMessageFactory.moviesNotFound()
            }

            is AppException.ApiException.Unauthorized -> {
                GenericErrorMessageFactory.authenticationError()
            }

            // Global: Infrastructure errors via enum
            else -> {
                exception.toGeneralErrorTypeOrNull()?.let { generalType ->
                    GenericErrorMessageFactory.createFrom(generalType, exception.userMessage)
                } ?: GenericErrorMessageFactory.unknownError(exception.userMessage)
            }
        }
    }

    // ==================== STATE HELPERS ====================
    private fun updateCategorySuccess(
        categoryType: MovieCategoryType,
        movies: List<Movie>,
    ) {
        val currentState = _uiState.value

        when(currentState){
            is HomeUiState.Loading -> {
                // Create initial success state
                val newContent =  createContentWithSuccessfulCategory(
                    baseContent = HomeContent.empty(),
                    categoryType = categoryType,
                    movies = movies
                )
                _uiState.value = HomeUiState.Success(content = newContent)
            }

            is HomeUiState.Success -> {
                // update existing content
                val updatedContent = createContentWithSuccessfulCategory(
                    baseContent = currentState.content,
                    categoryType = categoryType,
                    movies = movies
                )
                _uiState.value = currentState.copy(content = updatedContent)
            }
            is HomeUiState.Error -> {
                // Transition from error to success
                val newContent = createContentWithSuccessfulCategory(
                    baseContent = com.mustafakocer.feature_movies.home.domain.model.HomeContent.empty(),
                    categoryType = categoryType,
                    movies = movies
                )
                _uiState.value = HomeUiState.Success(content = newContent)
            }
        }
    }


    private fun createContentWithSuccessfulCategory(
        baseContent: com.mustafakocer.feature_movies.home.domain.model.HomeContent,
        categoryType: MovieCategoryType,
        movies: List<com.mustafakocer.feature_movies.home.domain.model.Movie>,
    ): com.mustafakocer.feature_movies.home.domain.model.HomeContent {
        // ✅ BUSINESS LOGIC: Create successful section
        val successSection = com.mustafakocer.feature_movies.home.domain.model.MovieSection.success(
            categoryType,
            movies
        )

        // ✅ PURE DATA OPERATION: Update content
        return baseContent.updateSection(categoryType, successSection)
    }

    private fun removeFromRetrying(categoryType: MovieCategoryType) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(
                retryingCategories = currentState.retryingCategories - categoryType
            )
        }
    }

    private fun showSuccessMessage(message: String) {
        viewModelScope.launch {
            _uiEffect.emit(HomeEffect.ShowToast(message))
        }
    }

    private fun showErrorMessage(message: String) {
        viewModelScope.launch {
            _uiEffect.emit(HomeEffect.ShowToast(message))
        }
    }
}