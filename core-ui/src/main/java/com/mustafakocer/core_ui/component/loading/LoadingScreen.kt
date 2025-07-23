package com.mustafakocer.core_ui.component.loading

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

/**
 * A highly reusable Composable that displays a dynamic list of shimmering placeholders,
 * automatically filling the screen based on item dimensions.
 *
 * @param modifier The modifier to be applied to the lazy list container.
 * @param itemHeight The height of a single placeholder item.
 * @param itemWidth The width of a single placeholder item.
 * @param orientation The orientation of the list (Vertical or Horizontal).
 * @param contentPadding Padding to apply to the content of the lazy list.
 * @param skeletonContent The Composable lambda that defines the appearance of a single placeholder item.
 *
 * Architectural Note:
 * This component provides a polished and informative loading state, replacing blank screens or
 * simple spinners. Its key architectural advantage is its dynamic and reusable design. It uses a
 * slot-based API (`skeletonContent`) to allow any placeholder shape, while automatically
 * calculating the required item count to fill the screen. This adapts to any device size and
 * orientation, centralizing the shimmer logic and drastically reducing boilerplate in feature screens.
 */
@Composable
fun ShimmerLoadingScreen(
    modifier: Modifier = Modifier,
    itemHeight: Dp,
    itemWidth: Dp,
    orientation: Orientation = Orientation.Vertical,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    skeletonContent: @Composable () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val itemCount = remember(configuration, itemHeight, itemWidth, orientation) {
        val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
        val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
        val itemHeightPx = with(density) { itemHeight.toPx() }
        val itemWidthPx = with(density) { itemWidth.toPx() }

        if (orientation == Orientation.Vertical) {
            // Why `+ 1`: This ensures we render enough items to fill the entire screen,
            // including a partially visible item at the bottom, preventing visual gaps.
            ceil(screenHeightPx / itemHeightPx).toInt() + 1
        } else {
            if (itemWidthPx > 0) {
                ceil(screenWidthPx / itemWidthPx).toInt() + 1
            } else {
                // Fallback for cases where width might not be available immediately.
                10
            }
        }
    }

    if (orientation == Orientation.Vertical) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            userScrollEnabled = false
        ) {
            items(itemCount) {
                skeletonContent()
            }
        }
    } else {
        LazyRow(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            userScrollEnabled = false
        ) {
            items(itemCount) {
                skeletonContent()
            }
        }
    }
}