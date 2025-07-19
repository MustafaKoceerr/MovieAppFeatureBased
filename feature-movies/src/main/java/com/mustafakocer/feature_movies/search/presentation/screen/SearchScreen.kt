package com.mustafakocer.feature_movies.search.presentation.screen

import androidx.compose.foundation.layout.Column
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
import com.mustafakocer.feature_movies.search.presentation.components.SearchInitialPrompt
import com.mustafakocer.feature_movies.search.presentation.components.SearchTopBar
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEvent
import com.mustafakocer.feature_movies.search.presentation.contract.SearchUiState
import com.mustafakocer.feature_movies.shared.presentation.components.commonpagination.MovieListContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchUiState,
    onEvent: (SearchEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SearchTopBar(
                searchQuery = state.searchQuery,
                onQueryChange = { query -> onEvent(SearchEvent.QueryChanged(query)) },
                onClearSearch = { onEvent(SearchEvent.ClearSearch) },
                onNavigateBack = { onEvent(SearchEvent.BackClicked) },
                onSearchSubmitted = { onEvent(SearchEvent.SearchSubmitted) }
            )
        }
    ) { paddingValues ->

        val lazyMovieItems = state.searchResults.collectAsLazyPagingItems()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Arama kutusu boşsa veya arama yapmak için yeterli karakter yoksa,
            // kullanıcıya bir başlangıç mesajı gösteriyoruz.
            if (state.showInitialPrompt || !state.canSearch) {
                SearchInitialPrompt()
            } else {
                // Arama yapıldıysa, Paging'in durumunu kontrol ediyoruz.
                when (val refreshState = lazyMovieItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        LoadingScreen()
                    }

                    is LoadState.Error -> {
                        val appException = refreshState.error.toAppException()
                        ErrorScreen(
                            error = appException.toErrorInfo(),
                            onRetry = { lazyMovieItems.retry() }
                        )
                    }

                    is LoadState.NotLoading -> {
                        // MovieListContent, kendi içinde boş liste durumunu da yönetir.
                        MovieListContent(
                            lazyMovieItems = lazyMovieItems,
                            onMovieClick = { movie ->
                                onEvent(SearchEvent.MovieClicked(movie.id))
                            }
                        )
                    }
                }
            }
        }
    }
}