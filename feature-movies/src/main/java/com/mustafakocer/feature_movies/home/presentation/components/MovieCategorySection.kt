package com.mustafakocer.feature_movies.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.error.ErrorCard
import com.mustafakocer.feature_movies.home.domain.model.Movie
import com.mustafakocer.feature_movies.home.domain.model.MovieSection

@Composable
fun MovieCategorySection(
    section: MovieSection,
    onMovieClick: (Movie) -> Unit,
    onViewAllClick: () -> Unit,
    onRetryClick: (() -> Unit)? = null,
    isRetrying: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.category.displayName,
                style = MaterialTheme.typography.headlineSmall
            )

            // Only show "View All" if section has movies and no error
            if (section.movies.isNotEmpty() && section.error == null && !isRetrying) {
                TextButton(onClick = onViewAllClick) {
                    Text("View All")
                }
            }
        }

        // Section Content
        when {
            // Loading State (initial loading or retrying)
            section.isLoading || isRetrying -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = if (isRetrying)
                                "Retrying ${section.category.displayName.lowercase()}..."
                            else
                                "Loading ${section.category.displayName.lowercase()}...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Error State
            section.error != null -> {
                ErrorCard(
                    title = "${section.category.displayName} Failed",
                    message = section.error,
                    onRetry = onRetryClick
                )
            }

            // Success State with Movies
            section.movies.isNotEmpty() -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(section.movies) { movie ->
                        MovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie) }
                        )
                    }
                }
            }

            // Empty State (no movies, no loading, no error)
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No ${section.category.displayName.lowercase()} available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}