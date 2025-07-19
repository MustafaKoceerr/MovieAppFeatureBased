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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.error.toErrorInfo
import com.mustafakocer.core_ui.component.loading.LoadingScreen
import com.mustafakocer.feature_movies.details.presentation.components.MovieDetailsContent
import com.mustafakocer.feature_movies.details.presentation.components.MovieDetailsTopBar
import com.mustafakocer.feature_movies.details.presentation.components.ShareFloatingActionButton
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsUiState
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.details.util.formatShareContent

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
                title = state.movie?.title ?: stringResource(R.string.movie_details),
                // isOffline durumunu şimdilik kaldırdık, daha sonra eklenebilir.
                onNavigateBack = { onEvent(MovieDetailsEvent.BackPressed) },
            )
        },
        floatingActionButton = {
            // Sadece film detayı varsa paylaşma butonu görünsün.
            if (state.movie != null) {
                val shareContent = formatShareContent(movie = state.movie)
                ShareFloatingActionButton(
                    isSharing = state.isSharing,
                    onClick = {
                        onEvent(
                            MovieDetailsEvent.ShareMovie(content = shareContent)
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            if (state.movie != null) {
                MovieDetailsContent(
                    movie = state.movie,
                    isRefreshLoading = state.isRefreshing
                )
            } else if (state.isLoading) {
                LoadingScreen(message = stringResource(R.string.loading_movie_details))
            } else if (state.error != null) {
                ErrorScreen(
                    error = state.error.toErrorInfo(),
                    onRetry = { onEvent(MovieDetailsEvent.Refresh) }
                )
            }
        }
    }
}