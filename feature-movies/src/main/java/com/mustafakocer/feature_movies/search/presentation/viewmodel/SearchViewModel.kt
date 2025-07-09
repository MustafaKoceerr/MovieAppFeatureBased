package com.mustafakocer.feature_movies.search.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.feature_movies.search.domain.model.SearchQuery
import com.mustafakocer.feature_movies.search.domain.usecase.SearchMoviesUseCase
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEffect
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEvent
import com.mustafakocer.feature_movies.search.presentation.contract.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
) : ViewModel(), UiContract<SearchUiState, SearchEvent, SearchEffect> {
    // MVI STATE MANAGEMENT
    private val _uiState = MutableStateFlow(SearchUiState())
    override val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SearchEffect>()
    override val uiEffect: SharedFlow<SearchEffect> = _uiEffect.asSharedFlow()

    // ==================== AUTO SEARCH PROPERTIES ====================
    private var searchJob: Job? = null
    private val searchDebounceDelayMs = 600L // 600ms delay
    private val minAutoSearchLength = 3 // Start auto search after 3 characters
    // ==================== EVENT HANDLING ====================

    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> handleQueryChanged(event.query)
            is SearchEvent.SearchTriggered -> handleSearchTriggered()
            is SearchEvent.ClearSearch -> handleClearSearch()
        }
    }

    // ==================== PRIVATE EVENT HANDLERS ====================
// ==================== PUBLIC NAVIGATION METHODS ====================

    /**
     * Navigate to movie details screen
     */
    fun navigateToMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _uiEffect.emit(SearchEffect.NavigateToMovieDetail(movieId))
        }
    }

    /**
     * Navigate back to previous screen
     */
    fun navigateBack() {
        viewModelScope.launch {
            _uiEffect.emit(SearchEffect.NavigateBack)
        }
    }

    /**
     * Show snackbar message
     */
    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _uiEffect.emit(SearchEffect.ShowSnackbar(message))
        }
    }

    /**
     * Hide keyboard
     */
    fun hideKeyboard() {
        viewModelScope.launch {
            _uiEffect.emit(SearchEffect.HideKeyboard)
        }
    }

    /**
     * ✅ SOLUTION: Update query immediately, handle search separately
     */
    private fun handleQueryChanged(newQuery: String) {
        // 1. Update query immediately for UI responsiveness
        _uiState.value = _uiState.value.copy(searchQuery = newQuery)

        // 2. Handle search logic separately
        searchWithDebounce()
    }

    /**
     * Handle search trigger (user pressed search button or enter)
     */
    private fun handleSearchTriggered() {
        val currentQuery = _uiState.value.searchQuery

        // Validate query
        if (!_uiState.value.canSearch) {
            showSnackbar("Please enter at least 2 characters")
            return
        }

        // Cancel any pending auto search and search immediately
        searchJob?.cancel()

        // Hide keyboard and start search
        hideKeyboard()

        performSearch(currentQuery)
    }

    /**
     * Auto search with debouncing - triggers after user stops typing
     */
    private fun searchWithDebounce() {
        // Cancel previous search job
        searchJob?.cancel()

        val currentQuery = _uiState.value.searchQuery.trim()

        // Only auto search if query has minimum length
        if (currentQuery.length < minAutoSearchLength) {
            clearResultsOnly() // ✅ Clear results but keep query
            return
        }

        // Start new debounced search
        searchJob = viewModelScope.launch {
            delay(searchDebounceDelayMs)
            performSearch(currentQuery)
        }
    }

    /**
     * ✅ UPDATED: Clear results but preserve query
     */
    private fun clearResultsOnly() {
        _uiState.value = _uiState.value.copy(
            searchResults = emptyFlow(),
            hasSearched = false,
            isSearchActive = false,
            isLoading = false,
            error = null
        )
    }
    /**
     * Handle clear search
     */
    private fun handleClearSearch() {
        _uiState.value = SearchUiState() // Reset to initial state
    }

    // ==================== PRIVATE BUSINESS LOGIC ====================

    /**
     * Perform actual search using use case
     */
    private fun performSearch(query: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            isSearchActive = true,
            hasSearched = true,
            error = null
        )

        try {
            val searchResults = searchMoviesUseCase(query)
                .cachedIn(viewModelScope) // Important for Paging3 configuration changes

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                searchResults = searchResults,
                isSearchActive = true
            )
        } catch (exception: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Search failed: ${exception.message}",
                searchResults = emptyFlow()
            )

            showSnackbar("Search failed. Please try again.")

        }
    }

    // ==================== PUBLIC HELPER METHODS ====================
    /**
     * Retry search (for error states)
     */
    fun retrySearch() {
        val currentQuery = _uiState.value.searchQuery
        if (currentQuery.isNotEmpty()) {
            performSearch(currentQuery)
        }
    }

    /**
     * Check if currently searching
     */
    val isSearching: Boolean
        get() = _uiState.value.isSearchActive && _uiState.value.isLoading

    override fun onCleared() {
        super.onCleared()
        // Cancel search job when ViewModel is cleared
        searchJob?.cancel()
    }
}