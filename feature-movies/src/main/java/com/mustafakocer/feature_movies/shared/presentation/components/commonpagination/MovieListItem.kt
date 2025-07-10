package com.mustafakocer.feature_movies.shared.presentation.components.commonpagination

/**
 * Tek bir film satırını çizen kart.
 */
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mustafakocer.feature_movies.shared.domain.model.MovieList

@Composable
fun MovieListItem(
    movie: MovieList,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    posterSize: PosterSize = PosterSize.Medium,
    maxOverviewLines: Int = 3,
    showVoteCount: Boolean = true,
    elevation: androidx.compose.ui.unit.Dp = 4.dp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoviePoster(
                posterPath = movie.posterPath,
                contentDescription = movie.title,
                size = posterSize
            )

            MovieInfo(
                title = movie.title,
                releaseDate = movie.releaseDate,
                overview = movie.overview,
                voteAverage = movie.voteAverage,
                voteCount = movie.voteCount,
                modifier = Modifier.weight(1f),
                maxOverviewLines = maxOverviewLines,
                showVoteCount = showVoteCount
            )
        }
    }
}