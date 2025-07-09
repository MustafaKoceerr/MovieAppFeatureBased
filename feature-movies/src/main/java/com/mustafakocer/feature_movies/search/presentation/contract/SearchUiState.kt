package com.mustafakocer.feature_movies.search.presentation.contract

import androidx.paging.PagingData
import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Search UI State - KISS principle
 *
 * CLEAN ARCHITECTURE: Presentation Layer - UI State
 * RESPONSIBILITY: Represent search screen state
 */
data class SearchUiState(
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val searchResults: Flow<PagingData<MovieList>> = emptyFlow(),
    val hasSearched: Boolean = false,
) : BaseUiState {

    /**
     * Check if search input is valid for searching
     */
    val canSearch: Boolean
        get() = searchQuery.trim().length >= 2

    /**
     * Check if we should show search results
     */
    val shouldShowResults: Boolean
        get() = hasSearched && isSearchActive

    /**
     * Check if we should show empty search state
     */
    val shouldShowEmptyState: Boolean
        get() = searchQuery.isEmpty() && !hasSearched
}