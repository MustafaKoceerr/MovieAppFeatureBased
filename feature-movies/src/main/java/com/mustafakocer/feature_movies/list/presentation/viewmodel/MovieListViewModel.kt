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
 * Movie List ViewModel
 *
 * Sorumlulukları:
 * - Gerekli kategori bilgisini SavedStateHandle'dan alır.
 * - Dil değişikliklerine duyarlı olan GetMovieListUseCase'i çağırır.
 * - PagingData akışını `cachedIn` ile yönetir ve UI state'e koyar.
 * - UI event'lerini (tıklama, geri gitme) ilgili Effect'lere dönüştürür.
 */
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getMovieListUseCase: GetMovieListUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MovieListUiState, MovieListEvent, MovieListEffect>(
    initialState = MovieListUiState()
) {
    // Kategori bilgisi navigasyon argümanından alınır.
    private val categoryEndpoint: String = savedStateHandle[MovieListScreen.KEY_CATEGORY_ENDPOINT]
        ?: throw IllegalStateException("Category endpoint is required for MovieListScreen")

    init {
        // Kategori endpoint'ini MovieCategory enum'una çevir.
        val category = MovieCategory.fromApiEndpoint(categoryEndpoint)

        if (category == null) {
            // Geçersiz bir kategori gelirse, hata mesajı göster ve ekranı kapat.
            sendEffect(MovieListEffect.ShowSnackbar("Category not found."))
            sendEffect(MovieListEffect.NavigateBack)
        } else {
            // Geçerli kategori ile film listesini yükle.
            loadMovies(category)
        }
    }


    override fun onEvent(event: MovieListEvent) {
        when (event) {
            is MovieListEvent.MovieClicked -> {
                sendEffect(
                    MovieListEffect.NavigateToMovieDetail(
                        event.movieId
                    )
                )
            }

            is MovieListEvent.BackClicked -> {
                sendEffect(MovieListEffect.NavigateBack)
            }
        }
    }

    /**
     * Belirtilen kategori için film listesini yükler.
     * UseCase artık dil yönetimini kendi içinde yaptığı için dil parametresine gerek yoktur.
     */
    private fun loadMovies(category: MovieCategory) {
        val moviesPagingFlow = getMovieListUseCase(category)
            .cachedIn(viewModelScope) // Paging verisini ViewModel scope'unda önbelleğe al.

        setState {
            copy(
                movies = moviesPagingFlow,
                category = category
            )
        }
    }
}
