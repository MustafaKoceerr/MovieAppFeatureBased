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
import androidx.paging.compose.collectAsLazyPagingItems
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
                title = state.categoryTitle,
                onNavigateBack = { onEvent(MovieListEvent.BackClicked) },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. ViewModel'den gelen Flow'u, UI'ın kullanabileceği LazyPagingItems'e dönüştürüyoruz.
            //    Asıl veri toplama ve sayfa yükleme işini bu yapar.
            val lazyMovieItems = state.movies.collectAsLazyPagingItems()

            // 2. MovieListContent, Paging'in kendi durumlarını yönetir.
            //    (Yükleme, Hata, Boş Liste vb.)
            MovieListContent(
                lazyMovieItems = lazyMovieItems,
                onMovieClick = { movie ->
                    onEvent(MovieListEvent.MovieClicked(movie.id))
                }
            )
        }
    }
}