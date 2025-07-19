package com.mustafakocer.feature_movies.search.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.feature_movies.search.domain.usecase.SearchMoviesUseCase
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEffect
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEvent
import com.mustafakocer.feature_movies.search.presentation.contract.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : BaseViewModel<SearchUiState, SearchEvent, SearchEffect>(
    SearchUiState()
) {

    // Kullanıcının girdiği arama sorgusunu reaktif olarak işlemek için.
    private val searchQueryFlow = MutableStateFlow("")

    init {
        observeSearchQuery()
    }

    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                // UI'ı anında güncelle ve reaktif akışı tetikle.
                setState { copy(searchQuery = event.query) }
                searchQueryFlow.value = event.query
            }

            is SearchEvent.ClearSearch -> {
                // Arama kutusunu ve sonuçları temizle.
                setState {
                    copy(
                        searchQuery = "",
                        searchResults = emptyFlow()
                    )
                }
                searchQueryFlow.value = ""
            }

            is SearchEvent.MovieClicked -> {
                sendEffect(
                    SearchEffect.NavigateToMovieDetail(
                        event.movieId
                    )
                )
            }

            is SearchEvent.BackClicked -> {
                sendEffect(SearchEffect.NavigateBack)
            }

            is SearchEvent.SearchSubmitted -> {
                sendEffect(SearchEffect.HideKeyboard)
            }
        }
    }

    /**
     * Arama sorgusu akışını dinler ve debounce/filter mantığını uygular.
     */
    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(500L) // Kullanıcı yazmayı bıraktıktan sonra 500ms bekle
                .distinctUntilChanged() // Aynı sorguyu tekrar işleme
                .collectLatest { query ->
                    // Arama yapmak için yeterli uzunlukta mı diye kontrol et
                    if (query.trim().length >= 3) {
                        performSearch(query)
                    } else {
                        // Yeterli uzunlukta değilse, mevcut sonuçları temizle
                        setState {
                            copy(
                                searchResults = emptyFlow()
                            )
                        }
                    }
                }
        }
    }

    /**
     * Asıl arama işlemini gerçekleştirir ve Paging Flow'unu state'e koyar.
     */
    private fun performSearch(query: String) {
        val searchResultsFlow = searchMoviesUseCase(query).cachedIn(viewModelScope)
        setState { copy(searchResults = searchResultsFlow) }
    }

}