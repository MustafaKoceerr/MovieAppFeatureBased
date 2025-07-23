package com.mustafakocer.feature_movies.list.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.paging.compose.collectAsLazyPagingItems
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.home.presentation.screen.toLocalizedTitle
import com.mustafakocer.feature_movies.list.presentation.components.MovieListTopAppBar
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEvent
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListUiState
import com.mustafakocer.feature_movies.shared.presentation.components.commonpagination.HandlePagingLoadState
import com.mustafakocer.feature_movies.shared.presentation.components.list.PaginatedMovieList

// MovieListTopAppBar artık private olduğu için bu dosyada tanımlanmalı
// veya doğrudan Scaffold içinde yazılmalı. Temizlik için burada bırakalım.
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
            // Bu TopAppBar artık bu ekrana özel.
            MovieListTopAppBar(
                title = state.category?.toLocalizedTitle() ?: "",
                onNavigateBack = { onEvent(MovieListEvent.BackClicked) },
            )
        }
    ) { paddingValues ->
        val lazyMovieItems = state.movies.collectAsLazyPagingItems()

        // Tam ekran yükleme/hata durumlarını merkezi bileşenle yönet.
        HandlePagingLoadState(
            lazyPagingItems = lazyMovieItems,
            modifier = Modifier.fillMaxSize()
        ) { items ->
            // Başarılı durumda, sayfalanmış liste bileşenini göster.
            PaginatedMovieList(
                lazyPagingItems = items,
                onMovieClick = { movie ->
                    onEvent(MovieListEvent.MovieClicked(movie.id))
                },
                contentPadding = paddingValues
            )
        }
    }
}

