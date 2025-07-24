package com.mustafakocer.feature_movies.shared.presentation.components.commonpagination

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.mustafakocer.core_domain.exception.toAppException
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.error.toErrorInfo
import com.mustafakocer.core_ui.component.loading.ShimmerLoadingScreen
import com.mustafakocer.feature_movies.shared.presentation.components.atoms.MovieListItemSkeleton

/**
 * A centralized and reusable composable for handling the common load states of Jetpack Paging 3.
 *
 * Architectural Decision: This component abstracts away the boilerplate logic of handling loading,
 * error, and success states for any paginated list. By centralizing this logic, we ensure a
 * consistent user experience across all paginated screens (e.g., same loading shimmer, same error
 * screen) and keep the screen-level composables cleaner and more focused on their specific content.
 *
 * @param T The type of data contained within the [LazyPagingItems].
 * @param lazyPagingItems The Paging 3 data stream whose state will be managed.
 * @param modifier The modifier to be applied to this component.
 * @param content The composable content to be displayed when the data has been successfully loaded.
 *                This lambda receives the [LazyPagingItems] as a parameter, giving the caller
 *                access to the paginated data.
 */
@Composable
fun <T : Any> HandlePagingLoadState(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    content: @Composable (LazyPagingItems<T>) -> Unit,
) {
    // UI/UX Decision: `Crossfade` is used to provide a smooth transition between the loading
    // state (shimmer) and the content/error state. This avoids a jarring "pop-in" effect,
    // leading to a more polished user experience.
    val loadState = lazyPagingItems.loadState.refresh
    var showShimmer by remember { mutableStateOf(true) }

    // This effect ensures the `showShimmer` state, which drives the Crossfade, is correctly
    // updated whenever the Paging `loadState` changes.
    LaunchedEffect(loadState) {
        showShimmer = loadState is LoadState.Loading
    }

    Crossfade(
        targetState = showShimmer,
        animationSpec = tween(durationMillis = 500),
        label = "ContentCrossfade"
    ) { isLoading ->
        if (isLoading) {
            // Display a generic shimmer loading screen while waiting for the initial data.
            // The skeleton content is passed in, making this component adaptable.
            ShimmerLoadingScreen(
                itemHeight = 150.dp,
                itemWidth = 300.dp,
                skeletonContent = { MovieListItemSkeleton() }
            )
        } else {
            if (loadState is LoadState.Error) {

                val appException = loadState.error.toAppException()
                ErrorScreen(
                    modifier = modifier,
                    error = appException.toErrorInfo(),
                    onRetry = { lazyPagingItems.retry() }
                )
            } else {
                // If there is no loading and no error, we are in the success state.
                // The main content is displayed by invoking the content lambda.
                content(lazyPagingItems)
            }
        }
    }
}