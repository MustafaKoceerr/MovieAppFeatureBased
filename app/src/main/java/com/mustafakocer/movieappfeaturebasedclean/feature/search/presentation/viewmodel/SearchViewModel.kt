package com.mustafakocer.movieappfeaturebasedclean.feature.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.feature.search.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

private const val SEARCH_DEBOUNCE_MILLIS = 500L

/**
 * The text field value is a plain [StateFlow]; the paged results are a separate flow that
 * re-queries (debounced) whenever the text changes.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    searchRepository: SearchRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class)
    val searchResults: Flow<PagingData<MovieListItem>> = searchRepository.searchMovies(
        queryFlow = _searchQuery
            .debounce(SEARCH_DEBOUNCE_MILLIS)
            .distinctUntilChanged()
    ).cachedIn(viewModelScope)

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onClearSearch() {
        _searchQuery.value = ""
    }
}
