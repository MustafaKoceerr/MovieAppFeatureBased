package com.mustafakocer.feature_movies.details.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.error.toErrorInfo
import com.mustafakocer.feature_movies.details.presentation.components.MovieDetailsContent
import com.mustafakocer.feature_movies.details.presentation.components.MovieDetailsSkeleton
import com.mustafakocer.feature_movies.details.presentation.components.MovieDetailsTopBar
import com.mustafakocer.feature_movies.details.presentation.components.ShareFloatingActionButton
import com.mustafakocer.feature_movies.details.presentation.components.formatShareContent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsUiState

/**
 * A purely visual, "dumb" component that displays the UI for the movie details screen.
 *
 * @param state The current UI state to render.
 * @param onEvent A lambda to propagate user interactions up to the ViewModel.
 * @param snackbarHostState The state manager for displaying Snackbars.
 *
 * Architectural Note:
 * This Composable is responsible for the overall structure of the screen using `Scaffold`.
 * It acts as a dispatcher, deciding which main content to show based on the `state`:
 * - `isLoading`: Shows the `MovieDetailsSkeleton`.
 * - `movie != null`: Shows the `MovieDetailsContent`.
 * - `error != null`: Shows the `ErrorScreen`.
 * This approach keeps the component stateless and highly dependent on its inputs, making it
 * easy to preview and test different UI states.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    state: MovieDetailsUiState,
    onEvent: (MovieDetailsEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            MovieDetailsTopBar(
                title = state.movie?.title ?: "",
                onNavigateBack = { onEvent(MovieDetailsEvent.BackPressed) },
            )
        },
        floatingActionButton = {
            state.movie?.let { movie ->
                val shareContent = formatShareContent(movie = movie)
                ShareFloatingActionButton(
                    isSharing = state.isSharing,
                    onClick = { onEvent(MovieDetailsEvent.ShareMovie(content = shareContent)) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> MovieDetailsSkeleton()
                state.movie != null -> MovieDetailsContent(movie = state.movie, isRefreshLoading = state.isRefreshing)
                state.error != null -> ErrorScreen(
                    error = state.error.toErrorInfo(),
                    onRetry = { onEvent(MovieDetailsEvent.Refresh) }
                )
            }
        }
    }
}