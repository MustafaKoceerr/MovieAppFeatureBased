package com.mustafakocer.feature_movies.list.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
) {
    Scaffold(
        topBar = {
            MovieListTopAppBar(
                title = state.categoryTitle,
                onNavigateBack = { onEvent(MovieListEvent.NavigateBackClicked) },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val movies = state.movies.collectAsLazyPagingItems()
            MovieListContent(
                movies = movies,
                onMovieClick = { movie ->
                    onEvent(MovieListEvent.MovieClicked(movie.id))
                }
            )

        }
    }
}
