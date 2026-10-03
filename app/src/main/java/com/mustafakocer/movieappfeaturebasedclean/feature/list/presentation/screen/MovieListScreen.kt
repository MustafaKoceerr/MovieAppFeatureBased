package com.mustafakocer.movieappfeaturebasedclean.feature.list.presentation.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.mustafakocer.movieappfeaturebasedclean.feature.home.presentation.screen.toLocalizedTitle
import com.mustafakocer.movieappfeaturebasedclean.feature.list.presentation.components.MovieListTopAppBar
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieCategory
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.presentation.components.commonpagination.HandlePagingLoadState
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.presentation.components.list.PaginatedMovieList

/**
 * Stateless list screen: a top bar plus a paginated list of the category's movies.
 *
 * @param category The category being shown (used for the title).
 * @param movies The paged movies; load/error/empty states are handled by [HandlePagingLoadState].
 * @param onBackClick Called when the back arrow is pressed.
 * @param onMovieClick Called with the id of the tapped movie.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    category: MovieCategory?,
    movies: LazyPagingItems<MovieListItem>,
    onBackClick: () -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
) {
    Scaffold(
        topBar = {
            MovieListTopAppBar(
                title = category?.toLocalizedTitle() ?: "",
                onNavigateBack = onBackClick,
            )
        }
    ) { paddingValues ->
        HandlePagingLoadState(
            lazyPagingItems = movies,
            modifier = Modifier.fillMaxSize()
        ) {
            PaginatedMovieList(
                lazyPagingItems = movies,
                onMovieClick = { movie -> onMovieClick(movie.id) },
                contentPadding = paddingValues
            )
        }
    }
}
