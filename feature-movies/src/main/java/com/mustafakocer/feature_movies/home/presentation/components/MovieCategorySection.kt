package com.mustafakocer.feature_movies.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem

/**
 * A reusable composable that displays a single movie category (e.g., "Popular Movies")
 * with a horizontal list of its associated movies.
 *
 * @param categoryTitle The title of the category to be displayed.
 * @param movies The list of movies belonging to this category.
 * @param onMovieClick A callback invoked when a movie card is clicked, returning the movie's ID.
 * @param onViewAllClick A callback invoked when the "View All" button is clicked.
 * @param modifier The modifier to be applied to the root Column of the section.
 */
@Composable
fun MovieCategorySection(
    categoryTitle: String,
    movies: List<MovieListItem>,
    onMovieClick: (movieId: Int) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Architectural Decision: If the movie list for a category is empty, we render nothing.
    // This is a clean way to handle conditional UI, preventing empty sections from cluttering the screen
    // without requiring complex visibility logic in the parent composable.
    if (movies.isEmpty()) return

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryTitle,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f) // Ensures the title takes available space, pushing the button to the end.
            )
            TextButton(onClick = onViewAllClick) {
                Text(stringResource(R.string.view_all))
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = movies,
                // Performance Optimization: Providing a stable and unique key for each item
                // allows Jetpack Compose to intelligently handle recompositions. It can identify
                // specific items that have changed, moved, or been removed, preventing unnecessary
                // recomposition of the entire list.
                key = { movie -> movie.id }
            ) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }
    }
}