package com.mustafakocer.feature_movies.search.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.feature_movies.search.domain.usecase.SearchMoviesUseCase
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEffect
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEvent
import com.mustafakocer.feature_movies.search.presentation.contract.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : BaseViewModel<SearchUiState, SearchEvent, SearchEffect>(
    SearchUiState()
) {
    // Kullanıcının girdiği anlık sorguyu tutan StateFlow.
    private val searchQueryFlow = MutableStateFlow("")

    init {
        // ViewModel oluşturulduğunda, arama sorgusu akışını dinlemeye başla.
        observeSearchQuery()
    }

    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                // UI state'ini anında güncelle ve arama akışını tetikle.
                setState { copy(searchQuery = event.query) }
                searchQueryFlow.value = event.query
            }

            is SearchEvent.ClearSearch -> {
                setState { copy(searchQuery = "") }
                searchQueryFlow.value = ""
            }

            is SearchEvent.MovieClicked -> {
                sendEffect(SearchEffect.NavigateToMovieDetail(event.movieId))
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
     * Arama sorgusu akışını dinler ve UseCase'i çağırarak arama sonuçlarını günceller.
     */
    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        // UseCase artık hem dil hem de sorgu geçerliliği kontrolünü yaptığı için,
        // ViewModel'in tek görevi sorgu akışını UseCase'e bağlamaktır.
        val searchResultsFlow = searchMoviesUseCase(
            queryFlow = searchQueryFlow
                .debounce(500L) // Kullanıcı yazmayı bıraktıktan 500ms sonra arama yap.
                .distinctUntilChanged() // Aynı sorguyu tekrar arama.
        ).cachedIn(viewModelScope) // Sonuçları ViewModel scope'unda önbelleğe al.

        // Sonuç akışını UI state'ine ata.
        setState { copy(searchResults = searchResultsFlow) }
    }
}