package com.mustafakocer.feature_movies.home.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.error.toErrorInfo
import com.mustafakocer.core_ui.component.loading.LoadingScreen
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.home.presentation.components.FakeSearchBar
import com.mustafakocer.feature_movies.home.presentation.components.MovieCategorySection
import com.mustafakocer.feature_movies.home.presentation.contract.*
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { onEvent(HomeEvent.ProfileClicked) }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        // Ana kutu, tam ekran Yükleme veya Hata durumlarını yönetir.
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        )
        {
            // 1. Önce tam ekran hata durumunu kontrol et
            if (state.showFullScreenError) {
                // Hata null olmayacağı için !! kullanmak güvenlidir.
                ErrorScreen(
                    error = state.error!!.toErrorInfo(),
                    onRetry = { onEvent(HomeEvent.Refresh) }
                )
            }
            // 2. Sonra tam ekran yükleme durumunu kontrol et.
            else if (state.showFullScreenLoading) {
                LoadingScreen(message = stringResource(R.string.loading_movies))
            }
            // 3. Hiçbiri yoksa, asıl içerisi göster.
            else {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onEvent(HomeEvent.Refresh) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    HomeContent(state = state, onEvent = onEvent)
                }
            }
        }
    }
}

@Composable
private fun HomeContent(state: HomeUiState, onEvent: (HomeEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        FakeSearchBar(onClick = { onEvent(HomeEvent.SearchClicked) })

        Spacer(modifier = Modifier.height(16.dp))

        // Döngü, state'deki Map üzerinden kuruluyor.
        // Sıralama, enum'daki tanım sırasına göre otomatik olarak gelir.
        state.categories.forEach { (category, movies) ->
            MovieCategorySection(
                categoryTitle = category.toLocalizedTitle(),
                movies = movies,
                onMovieClick = { movieId -> onEvent(HomeEvent.MovieClicked(movieId)) },
                onViewAllClick = { onEvent(HomeEvent.ViewAllClicked(category)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun MovieCategory.toLocalizedTitle(): String {
    return when (this) {
        MovieCategory.NOW_PLAYING -> stringResource(id = R.string.category_title_now_playing)
        MovieCategory.POPULAR -> stringResource(id = R.string.category_title_popular)
        MovieCategory.TOP_RATED -> stringResource(id = R.string.category_title_top_rated)
        MovieCategory.UPCOMING -> stringResource(id = R.string.category_title_upcoming)
    }
}