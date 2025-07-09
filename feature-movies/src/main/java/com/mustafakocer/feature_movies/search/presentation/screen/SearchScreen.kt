package com.mustafakocer.feature_movies.search.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mustafakocer.feature_movies.search.presentation.viewmodel.SearchViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEffect
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEvent
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListUiState
import com.mustafakocer.feature_movies.search.presentation.components.SearchContent
import com.mustafakocer.feature_movies.search.presentation.components.SearchTopBar
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEffect
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEvent
import com.mustafakocer.feature_movies.search.presentation.contract.SearchUiState

/**
 * SearchScreen - Pure UI presentation
 *
 * CLEAN ARCHITECTURE: Presentation Layer - Pure UI
 * RESPONSIBILITY: Display search interface and handle user interactions
 *
 * FEATURES:
 * - Search input with validation
 * - Paginated search results
 * - Empty states and loading states
 * - Material 3 design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    contract: UiContract<SearchUiState, SearchEvent, SearchEffect>,
    snackbarHostState: SnackbarHostState,
) {
    val state by contract.uiState.collectAsStateWithLifecycle()
    val viewModel = contract as SearchViewModel // Cast for navigation methods

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            SearchTopBar(
                searchQuery = state.searchQuery,
                onQueryChange = { query ->
                    contract.onEvent(SearchEvent.QueryChanged(query))
                },
                onSearchTriggered = {
                    contract.onEvent(SearchEvent.SearchTriggered)
                },
                onClearSearch = {
                    contract.onEvent(SearchEvent.ClearSearch)
                },
                onNavigateBack = {
                    viewModel.navigateBack()
                },
                canSearch = state.canSearch,
                isLoading = state.isLoading
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Search results content
                val searchResults = state.searchResults.collectAsLazyPagingItems()

                SearchContent(
                    searchResults = searchResults,
                    searchState = state,
                    onMovieClick = { movieId ->
                        viewModel.navigateToMovieDetail(movieId)
                    },
                    onRetryClick = {
                        viewModel.retrySearch()
                    }
                )
            }
        }
    }
}
