package com.mustafakocer.feature_movies.details.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.shared.domain.model.Genre
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import com.mustafakocer.feature_movies.shared.util.ImageSize
import com.mustafakocer.feature_movies.shared.util.ImageUrlBuilder

/**
 * Ana içerik ve yenileme göstergesi için bir sarmalayıcı (wrapper).
 */
@Composable
fun MovieDetailsContent(
    movie: MovieDetails,
    isRefreshLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            MovieHeroSection(
                backdropUrl = ImageUrlBuilder.build(
                    path = movie.backdropUrl,
                    size = ImageSize.BACKDROP_W780
                ) ?: "",
                posterUrl = ImageUrlBuilder.build(
                    path = movie.posterUrl,
                    size = ImageSize.POSTER_W342
                ) ?: "",
                title = movie.title,
                tagline = if (movie.hasTagline) movie.tagline else null
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MovieStatsSection(
                    voteAverage = movie.voteAverage,
                    releaseDate = movie.releaseDate,
                    runtime = movie.runtime
                )
                if (movie.genres.isNotEmpty()) {
                    MovieGenresSection(genres = movie.genres)
                }
                MovieOverviewSection(overview = movie.overview)
            }

            Spacer(modifier = Modifier.height(80.dp)) // FAB için boşluk
        }

        if (isRefreshLoading) {
            RefreshLoadingIndicator(modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}



// --- İÇ IMPLEMENTASYON (PRIVATE) ---

@Composable
private fun MovieHeroSection(
    backdropUrl: String,
    posterUrl: String,
    title: String,
    tagline: String?,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AsyncImage(
            model = backdropUrl,
            contentDescription = stringResource(R.string.backdrop_description, title),
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        GradientOverlay()
        MovieInfoOverlay(posterUrl = posterUrl, title = title, tagline = tagline)
    }
}

@Composable
private fun GradientOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.background
                    ),
                    startY = 300f
                )
            )
    )
}

@Composable
private fun MovieInfoOverlay(
    posterUrl: String,
    title: String,
    tagline: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        AsyncImage(
            model = posterUrl,
            contentDescription = stringResource(R.string.poster_description, title),
            modifier = Modifier
                .width(120.dp)
                .aspectRatio(2 / 3f) // En/boy oranını koru
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            tagline?.let {
                Text(
                    text = "\"$it\"",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
private fun MovieOverviewSection(overview: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(), // Genişliği doldurmasını sağla
        verticalArrangement = Arrangement.spacedBy(12.dp) // Boşluğu biraz artır
    ) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge // Başlığı daha belirgin yap
        )
        if (overview.isNotBlank()) {
            Text(
                text = overview,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                // Satır aralığını artırarak okunabilirliği iyileştirir.
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
private fun RefreshLoadingIndicator(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(top = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
            Text(
                text = stringResource(R.string.refreshing),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MovieGenresSection(genres: List<Genre>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = stringResource(R.string.genres), style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            genres.forEach { genre ->
                AssistChip(
                    onClick = { /* TODO: Genre click event */ },
                    label = { Text(text = genre.name, style = MaterialTheme.typography.bodyMedium) }
                )
            }
        }
    }
}