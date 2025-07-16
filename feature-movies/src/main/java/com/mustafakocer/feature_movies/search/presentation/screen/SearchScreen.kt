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
                onNavigateBack = { onEvent(SearchEvent.BackClicked) }
            )
        }
    ) { paddingValues ->

        val searchResults = state.searchResults.collectAsLazyPagingItems()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Arama kutusu boşsa, kullanıcıya bir başlangıç mesajı gösteriyoruz.
            if (state.showInitialPrompt) {
                SearchInitialPrompt()
            } else {
                // Arama yapıldıysa, sonuçları gösteriyoruz.
                // MovieListContent, Paging'in kendi yükleme, hata ve boş durumlarını yönetir.
                MovieListContent(
                    lazyMovieItems = searchResults,
                    onMovieClick = { movie ->
                        onEvent(SearchEvent.MovieClicked(movie.id))
                    }
                )
            }
        }
    }
}