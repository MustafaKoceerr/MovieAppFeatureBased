package com.mustafakocer.feature_movies.list.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mustafakocer.core_common.exception.toAppException
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.error.toErrorInfo
import com.mustafakocer.core_ui.component.loading.LoadingScreen
import com.mustafakocer.feature_movies.home.presentation.screen.toLocalizedTitle
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEvent
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListUiState
import com.mustafakocer.feature_movies.shared.presentation.components.commonpagination.MovieListContent
import com.mustafakocer.feature_movies.shared.presentation.components.commonpagination.MovieListTopAppBar

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val lazyMovieItems = state.movies.collectAsLazyPagingItems()

            // Paging 3'ün ana yükleme durumunu (ilk yükleme veya tam ekran yenileme) kontrol et.
            when (val refreshState = lazyMovieItems.loadState.refresh) {
                // 1. TAM EKRAN YÜKLEME DURUMU
                is LoadState.Loading -> {
                    LoadingScreen()
                }
                // 2. TAM EKRAN HATA DURUMU
                is LoadState.Error -> {
                    // Paging'in fırlattığı Throwable'ı bizim AppException'ımıza çeviriyoruz.
                    val appException = refreshState.error.toAppException()
                    ErrorScreen(
                        error = appException.toErrorInfo(),
                        onRetry = { lazyMovieItems.retry() }
                    )
                }
                // 3. BAŞARILI VEYA BOŞ DURUM
                is LoadState.NotLoading -> {
                    // Veri varsa, MovieListContent'i göster.
                    // MovieListContent, kendi içinde sayfa sonu yükleme/hata durumlarını zaten yönetiyor.
                    MovieListContent(
                        lazyMovieItems = lazyMovieItems,
                        onMovieClick = { movie ->
                            onEvent(MovieListEvent.MovieClicked(movie.id))
                        }
                    )
                }
            }
        }
    }
}