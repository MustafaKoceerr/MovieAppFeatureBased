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
import com.mustafakocer.core_ui.component.loading.LoadingScreen
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.home.presentation.components.FakeSearchBar
import com.mustafakocer.feature_movies.home.presentation.components.MovieCategorySection
import com.mustafakocer.feature_movies.home.presentation.contract.*

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
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
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
            if (state.showFullScreenLoading) {
                LoadingScreen(message = stringResource(R.string.loading_movies))
            } else if (state.showFullScreenError) {
                ErrorScreen(
                    // errorInfo'yu anlık olarak oluşturabiliriz.
                    // error = state.error!!.toErrorInfoOrFallback(),
                    onRetry = { onEvent(HomeEvent.Refresh) }
                )
            } else {
                // Başarı durumunda, yenilenebilir içeriği gösteriyoruz.
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onEvent(HomeEvent.Refresh) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Buradaki içeriğin kaydırılabilir olması gereklidir.
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
                category = category,
                movies = movies,
                onMovieClick = { movieId -> onEvent(HomeEvent.MovieClicked(movieId)) },
                onViewAllClick = { onEvent(HomeEvent.ViewAllClicked(category)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}