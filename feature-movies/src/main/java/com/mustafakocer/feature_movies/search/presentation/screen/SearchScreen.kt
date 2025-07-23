package com.mustafakocer.feature_movies.search.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.mustafakocer.feature_movies.search.presentation.components.SearchInitialPrompt
import com.mustafakocer.feature_movies.search.presentation.components.SearchTopBar
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEvent
import com.mustafakocer.feature_movies.search.presentation.contract.SearchUiState
import com.mustafakocer.feature_movies.shared.presentation.components.commonpagination.HandlePagingLoadState
import com.mustafakocer.feature_movies.shared.presentation.components.list.PaginatedMovieList

/**
 * The main stateless UI component for the movie search screen.
 *
 * This composable is responsible for laying out the screen's visual elements based on the
 * provided [state] and forwarding user interactions to the ViewModel via the [onEvent] callback.
 * It contains no business logic itself.
 *
 * @param state The current [SearchUiState] to render.
 * @param onEvent A function to call when a user interaction occurs.
 * @param snackbarHostState The state manager for displaying Snackbars.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchUiState,
    onEvent: (SearchEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SearchTopBar(
                searchQuery = state.searchQuery,
                onQueryChange = { query -> onEvent(SearchEvent.QueryChanged(query)) },
                onClearSearch = { onEvent(SearchEvent.ClearSearch) },
                onNavigateBack = { onEvent(SearchEvent.BackClicked) },
                onSearchSubmitted = { onEvent(SearchEvent.SearchSubmitted) }
            )
        }
    ) { paddingValues ->
        val lazyMovieItems = state.searchResults.collectAsLazyPagingItems()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (state.showInitialPrompt || !state.canSearch) {
                SearchInitialPrompt()
            } else {
                // Architectural Decision: `HandlePagingLoadState` is a shared, reusable composable
                // that centralizes the logic for displaying loading spinners, error messages, or
                // an empty state based on the `LoadState` from the Paging library. This keeps
                // this screen-level composable clean and focused on the "success" state.
                HandlePagingLoadState(lazyPagingItems = lazyMovieItems) {
                    // This content lambda is only invoked when the paging data is successfully loaded.
                    // It displays the actual list of movies using another shared component.
                    PaginatedMovieList(
                        lazyPagingItems = lazyMovieItems,
                        onMovieClick = { movie ->
                            onEvent(SearchEvent.MovieClicked(movie.id))
                        }
                    )
                }
            }
        }
    }
}