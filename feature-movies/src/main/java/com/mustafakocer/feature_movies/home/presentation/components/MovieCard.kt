package com.mustafakocer.feature_movies.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import com.mustafakocer.feature_movies.shared.util.formattedRating
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.shared.util.ImageSize
import com.mustafakocer.feature_movies.shared.util.ImageUrlBuilder

/**
 * TEACHING MOMENT: Clean MovieCard with Utils
 *
 * ✅ IMPROVEMENTS:
 * - Uses getPosterUrl() extension
 * - Uses formattedRating extension
 * - Added TextOverflow.Ellipsis
 * - Removed unused imports
 * - Clean code with utils
 */
@Composable
fun MovieCard(
    movie: MovieListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(140.dp)
            .bounceClick()
    ) {
        Box {
            // Real Movie Poster with Extension
            AsyncImage(
                model = ImageUrlBuilder.build(
                    path = movie.posterUrl,
                    size = ImageSize.POSTER_W342
                ),
                contentDescription = stringResource(R.string.poster_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            // OKUNABİLİRLİK İÇİN GRADYAN
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            ),
                            startY = 300f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis // ✅ Ellipsis eklendi
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⭐ ${movie.voteAverage.formattedRating}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

