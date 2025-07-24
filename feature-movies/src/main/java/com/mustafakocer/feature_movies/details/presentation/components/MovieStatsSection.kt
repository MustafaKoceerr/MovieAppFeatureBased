package com.mustafakocer.feature_movies.details.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.shared.util.formattedRating

/**
 * A Composable that displays a row of key movie statistics (rating, release date, runtime).
 *
 * @param voteAverage The movie's average vote score.
 * @param releaseDate The movie's release date string.
 * @param runtime The movie's runtime in minutes.
 *
 * Architectural Note:
 * This component encapsulates the presentation logic for a specific, reusable piece of the UI.
 * It handles the conditional display of the runtime and uses `LocalContext` to access plural
 * string resources, a task appropriate for the UI layer.
 */
@Composable
fun MovieStatsSection(voteAverage: Double, releaseDate: String, runtime: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MovieStatItem(
            icon = Icons.Default.Star,
            label = stringResource(R.string.rating),
            value = voteAverage.formattedRating,
            modifier = Modifier.weight(1f)
        )
        MovieStatItem(
            icon = Icons.Default.DateRange,
            label = stringResource(R.string.release),
            value = releaseDate,
            modifier = Modifier.weight(1f)
        )
        if (runtime > 0) {
            val context = LocalContext.current
            // Why use plurals: This correctly formats the string for "1 minute" vs "120 minutes".
            val runtimeText = context.resources.getQuantityString(
                R.plurals.movie_runtime_in_minutes,
                runtime,
                runtime
            )
            MovieStatItem(
                icon = Icons.Default.Schedule,
                label = stringResource(R.string.runtime),
                value = runtimeText,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * A private helper composable for displaying a single stat item within a card.
 */
@Composable
private fun MovieStatItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}