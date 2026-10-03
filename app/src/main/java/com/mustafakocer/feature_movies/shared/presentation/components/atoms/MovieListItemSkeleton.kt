package com.mustafakocer.feature_movies.shared.presentation.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.loading.ShimmerBrush

/**
 * A skeleton composable that represents the loading state of a single movie item row
 * in a paginated list.
 *
 * Architectural Decision: This is an atomic "skeleton" component. Its purpose is to mimic the
 * layout and dimensions of the actual `MovieRow` component. By using this in a shimmer loading
 * screen, we provide the user with a visual placeholder that accurately represents the content
 * that is about to be loaded. This improves perceived performance and creates a more cohesive
 * loading experience.
 *
 * @param modifier The modifier to be applied to the root Row.
 */
@Composable
fun MovieListItemSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Skeleton for the movie poster.
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ShimmerBrush()) // The shimmer provides an animated loading effect.
        )
        // The spacer width is also matched with the real component's layout.
        Spacer(modifier = Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Skeleton for the movie title.
            // The fractional width (0.8f) mimics the approximate length of an average movie title.
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ShimmerBrush())
            )
            // Skeleton for the release date.
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.4f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ShimmerBrush())
            )
            // Skeleton for the movie overview/summary.
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ShimmerBrush())
            )
        }
    }
}