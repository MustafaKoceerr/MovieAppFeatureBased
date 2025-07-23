package com.mustafakocer.feature_movies.shared.presentation.components.atoms

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mustafakocer.feature_movies.shared.util.ImageSize
import com.mustafakocer.feature_movies.shared.util.ImageUrlBuilder

/**
 * A reusable, atomic component for displaying a movie poster.
 *
 * Architectural Decision: This is an "atomic" component in a design system. It has a single,
 * well-defined responsibility: to display a poster image. It is unaware of any business logic
 * or context beyond what is necessary to render the image, making it highly reusable across
 * different features (e.g., home screen, search results, movie details).
 *
 * @param posterPath The path segment of the poster image (not the full URL).
 * @param contentDescription The accessibility description for the poster image.
 * @param modifier The modifier to be applied to the AsyncImage.
 * @param size An enum that defines the display dimensions of the poster, ensuring consistency.
 */
@Composable
fun MoviePoster(
    posterPath: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: PosterSize = PosterSize.Medium,
) {

    val fullPosterUrl = ImageUrlBuilder.build(
        path = posterPath,
        size = ImageSize.POSTER_W342
    )

    AsyncImage(
        model = fullPosterUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .size(width = size.width, height = size.height)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

/**
 * Defines standardized poster dimensions to ensure visual consistency throughout the application.
 *
 * Using an enum for predefined sizes prevents developers from using arbitrary "magic numbers"
 * for dimensions, leading to a more maintainable and visually cohesive UI.
 */
enum class PosterSize(val width: Dp, val height: Dp) {
    Small(60.dp, 90.dp),
    Medium(80.dp, 120.dp),
    Large(120.dp, 180.dp)
}