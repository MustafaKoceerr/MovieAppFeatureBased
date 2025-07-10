// feature-movies/src/main/java/com/mustafakocer/feature_movies/details/presentation/screen/MovieDetailsScreen.kt
package com.mustafakocer.feature_movies.details.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.loading.LoadingScreen
import com.mustafakocer.feature_movies.details.presentation.components.MovieDetailsContent
import com.mustafakocer.feature_movies.details.presentation.components.MovieDetailsTopBar
import com.mustafakocer.feature_movies.details.presentation.components.ShareFloatingActionButton
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    // DEĞİŞİKLİK: İmza artık MovieListScreen ile tutarlı.
    state: MovieDetailsUiState,
    onEvent: (MovieDetailsEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(
                        top = 56.dp, // Account for TopAppBar height
                        start = 16.dp,
                        end = 16.dp
                    )
            )
        },
        topBar = {
            MovieDetailsTopBar(
                title = state.movieDetailsOrNull?.title ?: "Movie Details",
                isOffline = state.isOffline,
                // DEĞİŞİKLİK: Event'ler doğrudan 'onEvent' lambdası ile gönderiliyor.
                onNavigateBack = { onEvent(MovieDetailsEvent.BackPressed) },
            )
        },
        floatingActionButton = {
            if (state.hasMovieDetails) {
                ShareFloatingActionButton(
                    isSharing = state.isSharingInProgress,
                    onClick = { onEvent(MovieDetailsEvent.ShareMovie) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // State yönetimi mantığı aynı kalıyor, çünkü zaten temizdi.
            if (state is MovieDetailsUiState.Error) {
                ErrorScreen(
                    error = state.errorInfo,
                )
            } else if (state is MovieDetailsUiState.InitialLoading) {
                LoadingScreen(message = "Loading movie details...")
            } else if (state.hasMovieDetails) {
                MovieDetailsContent(
                    movieDetails = state.movieDetailsOrNull!!,
                    isRefreshLoading = state.isRefreshLoading,
                    isOffline = state.isOffline
                )
            }
        }
    }
}