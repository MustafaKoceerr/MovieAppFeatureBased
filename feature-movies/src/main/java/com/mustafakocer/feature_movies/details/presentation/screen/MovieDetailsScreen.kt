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
import com.mustafakocer.core_ui.component.error.toErrorInfoOrFallback
import com.mustafakocer.core_ui.component.loading.LoadingScreen
import com.mustafakocer.feature_movies.details.presentation.components.MovieDetailsContent
import com.mustafakocer.feature_movies.details.presentation.components.MovieDetailsTopBar
import com.mustafakocer.feature_movies.details.presentation.components.ShareFloatingActionButton
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsUiState
import com.mustafakocer.feature_movies.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    state: MovieDetailsUiState,
    onEvent: (MovieDetailsEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    // Event handler içinde string resources'ları alamadığım için burada tanımlıyorum.
    val shareTitle = stringResource(R.string.share_text_title)
    val textRating = stringResource(R.string.share_text_rating)
    val textRelease = stringResource(R.string.share_text_release)
    val textRuntime = stringResource(R.string.share_text_runtime)
    val textGenres = stringResource(R.string.share_text_genres)
    val textTags = stringResource(R.string.share_text_tags)

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
                ShareFloatingActionButton(
                    isSharing = state.isSharing,
                    onClick = {
                        onEvent(
                            MovieDetailsEvent.ShareMovie(
                                shareTitle = shareTitle,
                                textRating = textRating,
                                textRelease = textRelease,
                                textRuntime = textRuntime,
                                textGenres = textGenres,
                                textTags = textTags
                            )
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
            // Sealed class yerine basit if/else kontrolleri
            if (state.isLoading) {
                LoadingScreen(message = stringResource(R.string.loading_movie_details))
            } else if (state.error != null) {
                // Hata durumunda ErrorScreen gösterilir.
                // ErrorInfo'yu anlık olarak oluşturabiliriz veya state'e ekleyebiliriz.
                ErrorScreen(
                    error = state.error.toErrorInfoOrFallback(),
                    onRetry = { onEvent(MovieDetailsEvent.Refresh) }
                )
            } else if (state.movie != null) {
                // Başarılı durumda içerik gösterilir.
                MovieDetailsContent(
                    movie = state.movie,
                    isRefreshLoading = state.isRefreshing,
                )
            }
        }
    }
}