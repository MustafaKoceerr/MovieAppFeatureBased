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
import androidx.paging.compose.collectAsLazyPagingItems
import com.mustafakocer.feature_movies.search.presentation.components.SearchInitialPrompt
import com.mustafakocer.feature_movies.search.presentation.components.SearchTopBar
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEvent
import com.mustafakocer.feature_movies.search.presentation.contract.SearchUiState
import com.mustafakocer.feature_movies.shared.presentation.components.commonpagination.HandlePagingLoadState
import com.mustafakocer.feature_movies.shared.presentation.components.list.PaginatedMovieList

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
            if (state.showInitialPrompt || !state.canSearch) {
                SearchInitialPrompt()
            } else {
                // Tam ekran yükleme/hata durumlarını merkezi bileşenle yönet.
                HandlePagingLoadState(lazyPagingItems = lazyMovieItems) { items ->
                    // Başarılı durumda, sayfalanmış liste bileşenini göster.
                    PaginatedMovieList(
                        lazyPagingItems = items,
                        onMovieClick = { movie ->
                            onEvent(SearchEvent.MovieClicked(movie.id))
                        }
                        // Arama ekranında ekstra padding'e gerek yok, Column zaten yönetiyor.
                    )
                }
            }
        }
    }
}