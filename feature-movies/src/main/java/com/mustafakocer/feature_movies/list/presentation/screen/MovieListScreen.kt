package com.mustafakocer.feature_movies.list.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.mustafakocer.feature_movies.home.presentation.screen.toLocalizedTitle
import com.mustafakocer.feature_movies.list.presentation.components.MovieListTopAppBar
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEvent
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListUiState
import com.mustafakocer.feature_movies.shared.presentation.components.commonpagination.HandlePagingLoadState
import com.mustafakocer.feature_movies.shared.presentation.components.list.PaginatedMovieList

/**
 * The main stateless UI component for the movie list screen.
 *
 * This composable is responsible for laying out the screen's visual elements based on the
 * provided [state] and forwarding user interactions to the ViewModel via the [onEvent] callback.
 * It remains "dumb" by not containing any business logic.
 *
 * @param state The current [MovieListUiState] to render.
 * @param onEvent A function to call when a user interaction occurs.
 * @param snackbarHostState The state manager for displaying Snackbars.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    state: MovieListUiState,
    onEvent: (MovieListEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            MovieListTopAppBar(
                title = state.category?.toLocalizedTitle() ?: "",
                onNavigateBack = { onEvent(MovieListEvent.BackClicked) },
            )
        }
    ) { paddingValues ->

        val lazyMovieItems = state.movies.collectAsLazyPagingItems()

        HandlePagingLoadState(
            lazyPagingItems = lazyMovieItems,
            modifier = Modifier.fillMaxSize()
        ) {
            // This is the content lambda that `HandlePagingLoadState` will invoke only when the
            // paging data is successfully loaded and ready to be displayed.
            PaginatedMovieList(
                lazyPagingItems = lazyMovieItems,
                onMovieClick = { movie ->
                    onEvent(MovieListEvent.MovieClicked(movie.id))
                },
                contentPadding = paddingValues
            )
        }
    }
}