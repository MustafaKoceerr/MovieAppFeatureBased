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
import com.mustafakocer.feature_movies.shared.util.MovieConstants

/**
 * Film afişini gösteren, yeniden kullanılabilir atomik bir bileşen.
 *
 * @param posterPath Afişin yolu (path). Tam URL değil.
 * @param contentDescription Erişilebilirlik için afiş açıklaması.
 * @param size Poster boyutunu belirleyen enum.
 */
@Composable
fun MoviePoster(
    posterPath: String?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    size: PosterSize = PosterSize.Medium
) {
    // Not: Coil'in yeni versiyonlarında (coil3) model direkt URL bekler.
    // Bu yüzden base URL'i burada birleştiriyoruz.
    val fullPosterUrl = posterPath?.let { "${MovieConstants.IMAGE_BASE_URL}${MovieConstants.POSTER_SIZE}$it" }

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
 * Uygulama genelinde tutarlılık için standartlaştırılmış poster boyutları.
 */
enum class PosterSize(val width: Dp, val height: Dp) {
    Small(60.dp, 90.dp),
    Medium(80.dp, 120.dp),
    Large(120.dp, 180.dp)
}