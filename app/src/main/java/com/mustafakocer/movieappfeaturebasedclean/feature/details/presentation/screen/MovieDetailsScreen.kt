package com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.screen

import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.Genre
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieDetails
import androidx.compose.runtime.remember
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import androidx.compose.ui.tooling.preview.Preview
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
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.components.MovieDetailsContent
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.components.MovieDetailsSkeleton
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.components.MovieDetailsTopBar
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.components.ShareFloatingActionButton
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.components.formatShareContent
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.viewmodel.MovieDetailsUiState

/**
 * A purely visual, "dumb" component that displays the UI for the movie details screen.
 *
 * @param state The current UI state to render.
 * @param onBackClick Called when the back arrow is pressed.
 * @param onRefresh Called when the error retry button is pressed.
 * @param onShareClick Called with the formatted share text when the share button is pressed.
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
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onShareClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            MovieDetailsTopBar(
                title = state.movie?.title ?: "",
                onNavigateBack = onBackClick,
            )
        },
        floatingActionButton = {
            state.movie?.let { movie ->
                val shareContent = formatShareContent(movie = movie)
                ShareFloatingActionButton(
                    onClick = { onShareClick(shareContent) }
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
                    onRetry = onRefresh
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieDetailsScreenPreview() {
    MovieDiscoveryTheme {
        MovieDetailsScreen(
            state = MovieDetailsUiState(
                movie = MovieDetails(
                    id = 1,
                    title = "Preview Movie",
                    overview = "A longer overview that explains what the movie is about.",
                    posterUrl = "",
                    backdropUrl = "",
                    releaseDate = "2024-05-01",
                    voteAverage = 8.1,
                    runtime = 128,
                    tagline = "The best preview ever",
                    genres = listOf(Genre(1, "Action"), Genre(2, "Drama")),
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onRefresh = {},
            onShareClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieDetailsScreenLoadingPreview() {
    MovieDiscoveryTheme {
        MovieDetailsScreen(
            state = MovieDetailsUiState(isLoading = true),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onRefresh = {},
            onShareClick = {},
        )
    }
}
