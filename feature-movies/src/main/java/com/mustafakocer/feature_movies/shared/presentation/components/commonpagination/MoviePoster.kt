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
) {
    AsyncImage(
        model = "${MovieConstants.IMAGE_BASE_URL}${MovieConstants.POSTER_SIZE}${posterPath}",
        contentDescription = contentDescription,
        modifier = modifier
            .size(80.dp, 120.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}