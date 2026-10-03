package com.mustafakocer.movieappfeaturebasedclean.feature.search.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.presentation.components.commonpagination.HandlePagingLoadState
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.presentation.components.list.PaginatedMovieList
import com.mustafakocer.movieappfeaturebasedclean.feature.search.presentation.components.SearchInitialPrompt
import com.mustafakocer.movieappfeaturebasedclean.feature.search.presentation.components.SearchTopBar
import com.mustafakocer.movieappfeaturebasedclean.feature.search.domain.model.SearchQuery

/**
 * Stateless search screen: a search bar on top and either the initial prompt (query too short)
 * or the paged results below it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    searchResults: LazyPagingItems<MovieListItem>,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onSearchSubmitted: () -> Unit,
    onBackClick: () -> Unit,
    onMovieClick: (movieId: Int) -> Unit,
) {
    val canSearch = searchQuery.trim().length >= SearchQuery.MIN_LENGTH

    Scaffold(
        topBar = {
            SearchTopBar(
                searchQuery = searchQuery,
                onQueryChange = onQueryChange,
                onClearSearch = onClearSearch,
                onNavigateBack = onBackClick,
                onSearchSubmitted = onSearchSubmitted,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (canSearch) {
                HandlePagingLoadState(lazyPagingItems = searchResults) {
                    PaginatedMovieList(
                        lazyPagingItems = searchResults,
                        onMovieClick = { movie -> onMovieClick(movie.id) }
                    )
                }
            } else {
                SearchInitialPrompt()
            }
        }
    }
}
