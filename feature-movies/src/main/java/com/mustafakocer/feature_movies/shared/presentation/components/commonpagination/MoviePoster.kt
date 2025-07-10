package com.mustafakocer.feature_movies.shared.presentation.components.commonpagination

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mustafakocer.feature_movies.shared.util.MovieConstants

/**
 * Film afişini gösteren küçük bileşen.
 */
@Composable
fun MoviePoster(
    posterPath: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: PosterSize = PosterSize.Medium
) {
    AsyncImage(
        model = "${MovieConstants.IMAGE_BASE_URL}${MovieConstants.POSTER_SIZE}$posterPath",
        contentDescription = contentDescription,
        modifier = modifier
            .size(width = size.width, height = size.height)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

// Poster boyutları için enum
enum class PosterSize(val width: androidx.compose.ui.unit.Dp, val height: androidx.compose.ui.unit.Dp) {
    Small(60.dp, 90.dp),    // Search ekranı için
    Medium(80.dp, 120.dp),  // List ekranı için
    Large(120.dp, 180.dp)   // Detail ekranı için
}