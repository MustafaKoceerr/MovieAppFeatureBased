package com.mustafakocer.feature_movies.search.presentation.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.mustafakocer.core_ui.component.error.ErrorCard
import com.mustafakocer.core_ui.component.loading.LoadingIndicator
import com.mustafakocer.core_ui.component.loading.PageLoadingIndicator
import com.mustafakocer.feature_movies.search.presentation.contract.SearchUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieList

/**
 * Main search content area
 */
@Composable
fun SearchContent(
    searchResults: LazyPagingItems<MovieList>,
    searchState: SearchUiState,
    onMovieClick: (Int) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        // Empty search state
        searchState.shouldShowEmptyState -> {
            SearchEmptyState(
                modifier = modifier.fillMaxSize()
            )
        }

        // Show search results
        searchState.shouldShowResults -> {
            SearchResultsList(
                searchResults = searchResults,
                onMovieClick = onMovieClick,
                modifier = modifier
            )
        }
    }
}

/**
 * Empty search state
 */
@Composable
private fun SearchEmptyState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🔍",
                style = MaterialTheme.typography.displayMedium
            )

            Text(
                text = "Search for Movies",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Find your favorite movies by typing in the search box above",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Search results list with pagination
 */
@Composable
private fun SearchResultsList(
    searchResults: LazyPagingItems<MovieList>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = searchResults.itemCount,
            key = searchResults.itemKey { it.id }
        ) { index ->
            val movie = searchResults[index]
            if (movie != null) {
                SearchMovieItem(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }

        // Handle loading states
        when (searchResults.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    LoadingIndicator(
                        message = "Searching movies...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }
            }

            is LoadState.Error -> {
                val error = searchResults.loadState.refresh as LoadState.Error
                item {
                    ErrorCard(
                        title = "Search Failed",
                        message = error.error.localizedMessage ?: "Failed to search movies",
                        onRetry = { searchResults.retry() }
                    )
                }
            }

            else -> { /* Success */ }
        }

        // Handle append state (loading more pages)
        when (searchResults.loadState.append) {
            is LoadState.Loading -> {
                item {
                    PageLoadingIndicator()
                }
            }

            is LoadState.Error -> {
                val error = searchResults.loadState.append as LoadState.Error
                item {
                    ErrorCard(
                        title = "Failed to Load More",
                        message = error.error.localizedMessage ?: "Failed to load more results",
                        onRetry = { searchResults.retry() }
                    )
                }
            }

            else -> { /* Success or NotLoading */ }
        }
    }
}