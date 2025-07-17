package com.mustafakocer.feature_movies.search.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mustafakocer.core_common.exception.AppException
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
) : com.mustafakocer.core_android.presentation.BaseViewModel<SearchUiState, SearchEvent, SearchEffect>(SearchUiState()) {

    // Kullanıcının girdiği arama sorgusunu reaktif olarak işlemek için.
    private val searchQueryFlow = MutableStateFlow("")

    init {
        observeSearchQuery()
    }

    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                // UI'ı anında güncelle ve reaktif akışı tetikle.
                com.mustafakocer.core_android.presentation.BaseViewModel.setState { copy(searchQuery = event.query) }
                searchQueryFlow.value = event.query
            }

            is SearchEvent.ClearSearch -> {
                // Arama kutusunu ve sonuçları temizle.
                com.mustafakocer.core_android.presentation.BaseViewModel.setState {
                    copy(
                        searchQuery = "",
                        searchResults = emptyFlow()
                    )
                }
                searchQueryFlow.value = ""
            }

            is SearchEvent.MovieClicked -> {
                com.mustafakocer.core_android.presentation.BaseViewModel.sendEffect(
                    SearchEffect.NavigateToMovieDetail(
                        event.movieId
                    )
                )
            }

            is SearchEvent.BackClicked -> {
                com.mustafakocer.core_android.presentation.BaseViewModel.sendEffect(SearchEffect.NavigateBack)
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
                        com.mustafakocer.core_android.presentation.BaseViewModel.setState {
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
        // Paging'in kendi yükleme durumu olduğu için, executeSafeOnce'a gerek yok.
        // Paging Flow'unu oluşturup doğrudan state'e koyuyoruz.
        val searchResultsFlow = searchMoviesUseCase(query).cachedIn(viewModelScope)

        com.mustafakocer.core_android.presentation.BaseViewModel.setState { copy(searchResults = searchResultsFlow) }

        // Arama yapıldıktan sonra klavyeyi gizlemek iyi bir kullanıcı deneyimidir.
        com.mustafakocer.core_android.presentation.BaseViewModel.sendEffect(SearchEffect.HideKeyboard)
    }


    // Paging kendi durumlarını yönettiği için şimdilik basit kalabilirler.
    override fun handleError(error: AppException): SearchUiState {
        com.mustafakocer.core_android.presentation.BaseViewModel.sendEffect(
            SearchEffect.ShowSnackbar(
                error.userMessage
            )
        )
        return com.mustafakocer.core_android.presentation.BaseViewModel.currentState.copy(error = error)
    }

    override fun setLoading(loadingType: com.mustafakocer.core_android.presentation.LoadingType, isLoading: Boolean): SearchUiState {
        return com.mustafakocer.core_android.presentation.BaseViewModel.currentState.copy(isLoading = isLoading)
    }
}