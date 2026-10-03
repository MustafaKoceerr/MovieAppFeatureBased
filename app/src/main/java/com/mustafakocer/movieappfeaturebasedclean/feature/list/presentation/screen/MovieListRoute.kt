package com.mustafakocer.movieappfeaturebasedclean.feature.list.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.mustafakocer.movieappfeaturebasedclean.feature.list.presentation.viewmodel.MovieListViewModel

@Composable
fun MovieListRoute(
    onNavigateToMovieDetails: (movieId: Int) -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: MovieListViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel.category) {
        if (viewModel.category == null) onNavigateUp()
    }

    MovieListScreen(
        category = viewModel.category,
        movies = viewModel.movies.collectAsLazyPagingItems(),
        onBackClick = onNavigateUp,
        onMovieClick = onNavigateToMovieDetails,
    )
}
