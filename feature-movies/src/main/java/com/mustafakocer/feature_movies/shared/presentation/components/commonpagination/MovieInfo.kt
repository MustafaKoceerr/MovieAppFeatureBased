package com.mustafakocer.feature_movies.shared.presentation.components.commonpagination

/**
 * Film bilgilerini (başlık, özet vb.) gösteren bölüm.
 */
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.shared.util.formattedRating

@Composable
fun MovieInfo(
    title: String,
    releaseYear: String,
    overview: String,
    voteAverage: Double,
    voteCount: Int,
    modifier: Modifier = Modifier,
    maxOverviewLines: Int = 3,
    showVoteCount: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Film başlığı
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // Çıkış tarihi
        Text(
            text = releaseYear,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Film özeti
        Text(
            text = overview,
            style = MaterialTheme.typography.bodySmall,
            maxLines = maxOverviewLines,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Rating ve oy sayısı
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐ ${voteAverage.formattedRating}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (showVoteCount) {
                Text(
                    text = stringResource(R.string.votes, voteCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}