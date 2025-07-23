package com.mustafakocer.feature_movies.shared.presentation.components.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import com.mustafakocer.feature_movies.shared.presentation.components.atoms.MoviePoster
import com.mustafakocer.feature_movies.shared.presentation.components.atoms.PosterSize
import com.mustafakocer.feature_movies.shared.util.formattedRating
import kotlinx.coroutines.launch

/**
 * The primary composable for displaying a paginated list of movies from Paging 3.
 *
 * This component is responsible for rendering the list items and handling UI related to the list's
 * state, such as the "scroll to top" button and indicators for empty or appended content. It is
 * designed to be wrapped by `HandlePagingLoadState`, which manages the initial full-screen
 * loading and error states.
 *
 * @param lazyPagingItems The Paging 3 data stream, created using `.collectAsLazyPagingItems()` in the UI layer.
 * @param onMovieClick The callback to be invoked when a movie item is clicked.
 * @param modifier The modifier to be applied to this component.
 * @param contentPadding Padding values from a parent composable like `Scaffold`.
 */
@Composable
fun PaginatedMovieList(
    lazyPagingItems: LazyPagingItems<MovieListItem>,
    onMovieClick: (MovieListItem) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Architectural Decision: `derivedStateOf` is used for performance optimization. It ensures that
    // this block is only re-evaluated when the result of the calculation (`listState.firstVisibleItemIndex > 5`)
    // actually changes from true to false or vice versa. This prevents recomposition on every single
    // scroll event, which would be inefficient.
    val showScrollToTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 5
        }
    }

    // A `Box` is used as the root container to allow the `LazyColumn` and the `FloatingActionButton`
    // to be layered on top of each other.
    Box(modifier = modifier.padding(contentPadding)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // UI/UX Rationale: It's important to distinguish between a "loading" state and a
            // "successfully loaded but empty" state. This block handles the latter, providing
            // clear feedback to the user that the search was successful but yielded no results.
            if (lazyPagingItems.loadState.refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0) {
                item {
                    EmptyListIndicator(modifier = Modifier.fillParentMaxSize())
                }
            }

            // Render the list of movie items.
            items(
                count = lazyPagingItems.itemCount,
                // Performance Optimization: Providing a stable and unique key for each item allows
                // Compose to be more intelligent about recompositions, moves, and deletions.
                key = lazyPagingItems.itemKey { it.id }
            ) { index ->
                lazyPagingItems[index]?.let { movie ->
                    MovieRow(
                        movie = movie,
                        onClick = { onMovieClick(movie) }
                    )
                }
            }

            // Render the indicator for appending new pages at the end of the list.
            item {
                PagingAppendIndicator(lazyPagingItems = lazyPagingItems)
            }
        }

        // The "Scroll to Top" button is aligned to the bottom end of the Box.
        ScrollToTopButton(
            isVisible = showScrollToTopButton,
            onClick = {
                coroutineScope.launch {
                    // `animateScrollToItem` provides a smooth scrolling animation to the top.
                    listState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

/**
 * A FloatingActionButton that appears with an animation when the user scrolls down the list.
 *
 * @param isVisible Controls the visibility of the button.
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 */
@Composable
private fun ScrollToTopButton(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // UI/UX Decision: `AnimatedVisibility` provides a smooth fade and slide animation as the
    // button appears and disappears, making the UI feel more polished and less abrupt.
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(R.string.scroll_to_top)
            )
        }
    }
}

/**
 * Renders a single row in the movie list, containing a poster and movie information.
 *
 * @param movie The movie data to display.
 * @param onClick The callback to be invoked when the row is clicked.
 * @param modifier The modifier to be applied to the Card.
 */
@Composable
private fun MovieRow(
    movie: MovieListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoviePoster(
                posterPath = movie.posterUrl,
                contentDescription = movie.title,
                size = PosterSize.Medium
            )
            MovieInfo(
                movie = movie,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Renders the textual information for a movie, such as title, year, overview, and rating.
 *
 * @param movie The movie data to display.
 * @param modifier The modifier to be applied to the Column.
 */
@Composable
private fun MovieInfo(
    movie: MovieListItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis // Prevents long titles from breaking the layout.
        )
        Text(
            text = movie.releaseYear,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = movie.overview,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis, // Prevents long overviews from taking up too much space.
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐ ${movie.voteAverage.formattedRating}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.votes, movie.voteCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Displays an indicator at the bottom of the list based on the Paging `append` state.
 *
 * This component shows a loading spinner when the next page is being fetched, or an error
 * message with a retry button if the fetch fails.
 *
 * @param lazyPagingItems The Paging 3 data stream.
 * @param modifier The modifier to be applied to the indicator's container.
 */
@Composable
private fun PagingAppendIndicator(
    lazyPagingItems: LazyPagingItems<*>,
    modifier: Modifier = Modifier,
) {
    // Architectural Decision: This `when` statement specifically checks the `loadState.append`.
    // It is only responsible for the UI at the *end* of the list, not the initial full-screen load.
    // This separation of concerns makes the component focused and reusable.
    when (lazyPagingItems.loadState.append) {
        is LoadState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
            }
        }
        is LoadState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.error_loading_more),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Button(onClick = { lazyPagingItems.retry() }) {
                    Text(stringResource(R.string.try_again))
                }
            }
        }
        is LoadState.NotLoading -> {
            // Do nothing when not loading and no error.
        }
    }
}

/**
 * Displays a simple message indicating that the list is empty.
 *
 * @param modifier The modifier to be applied to the container Box.
 */
@Composable
private fun EmptyListIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_movies_found),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}