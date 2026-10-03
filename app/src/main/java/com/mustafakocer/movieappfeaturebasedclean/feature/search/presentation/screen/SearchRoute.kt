package com.mustafakocer.movieappfeaturebasedclean.feature.search.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.mustafakocer.movieappfeaturebasedclean.feature.search.presentation.viewmodel.SearchViewModel

@Composable
fun SearchRoute(
    onNavigateToMovieDetails: (movieId: Int) -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    SearchScreen(
        searchQuery = searchQuery,
        searchResults = searchResults,
        onQueryChange = viewModel::onQueryChange,
        onClearSearch = viewModel::onClearSearch,
        onSearchSubmitted = {
            keyboardController?.hide()
            focusManager.clearFocus()
        },
        onBackClick = onNavigateUp,
        onMovieClick = onNavigateToMovieDetails,
    )
}
