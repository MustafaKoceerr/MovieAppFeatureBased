package com.mustafakocer.feature_movies.list.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseViewModel
import com.mustafakocer.core_common.presentation.LoadingType
import com.mustafakocer.data_common.preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.list.domain.usecase.GetMovieListUseCase
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEffect
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEvent
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListUiState
import com.mustafakocer.navigation_contracts.navigation.MovieListScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
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
    private val languageRepository: LanguageRepository, // <-- 1. YENİ BAĞIMLILIK
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<MovieListUiState, MovieListEvent, MovieListEffect>(
    initialState = MovieListUiState()
) {
    private val categoryEndpoint: String? = savedStateHandle[MovieListScreen.KEY_CATEGORY_ENDPOINT]
    private val category: MovieCategory? =
        categoryEndpoint?.let { MovieCategory.fromApiEndpoint(it) }


    private fun initScreen() {
        if (category == null) {
            // Kategori bilgisi yoksa hata ver ve geri dön.
            sendEffect(MovieListEffect.ShowSnackbar("Category isn't found.")) // Bunu da UiText ile yapmalıyız :)
            sendEffect(MovieListEffect.NavigateBack)
        } else {
            viewModelScope.launch {
                languageRepository.languageFlow
                    .distinctUntilChanged()
                    .collect { language ->
                        // Her dil değişikliğinde, yeni dil ve mevcut kategori ile filmleri yükle.
                        loadMovies(category, language.apiParam)
                    }
            }
        }
    }

    init {
        initScreen()
    }

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

    private fun loadMovies(category: MovieCategory, language: String) {
        try {
            val moviesPagingFlow = getMovieListUseCase(category, language).cachedIn(viewModelScope)

            setState {
                copy(
                    movies = moviesPagingFlow,
                    category = category
                )
            }
        } catch (e: Exception) {
        }
    }

    // şimdilik boş kalabilir veya basitçe mevcut state'i döndürebilir.
    override fun handleError(error: AppException): MovieListUiState {
        // Paging dışı bir hata olursa diye, bir snackbar gösterebiliriz.
        sendEffect(MovieListEffect.ShowSnackbar(error.userMessage))
        return currentState
    }

    override fun setLoading(loadingType: LoadingType, isLoading: Boolean): MovieListUiState {
        // Paging dışı bir işlem için (örn. favorilere ekleme) kullanılabilir.
        return currentState.copy(isLoading = isLoading)
    }
}
