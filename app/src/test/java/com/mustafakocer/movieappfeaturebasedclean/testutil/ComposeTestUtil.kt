package com.mustafakocer.movieappfeaturebasedclean.testutil

import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.flowOf

/** Reads a string resource through the test activity (English, see the test JVM arguments). */
fun AndroidComposeTestRule<*, ComponentActivity>.string(@StringRes id: Int, vararg args: Any): String =
    activity.getString(id, *args)

/** Waits until at least one node with exactly [text] exists (paging content arrives asynchronously). */
fun AndroidComposeTestRule<*, ComponentActivity>.waitUntilTextExists(text: String, timeoutMillis: Long = 5_000) {
    waitUntil(timeoutMillis) { onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
}

/** Static paged data for screens that take [LazyPagingItems]. */
@Composable
fun pagingItemsOf(
    items: List<MovieListItem>,
    refresh: LoadState = LoadState.NotLoading(endOfPaginationReached = true),
): LazyPagingItems<MovieListItem> {
    val flow = remember(items, refresh) {
        val notLoading = LoadState.NotLoading(endOfPaginationReached = true)
        flowOf(
            PagingData.from(
                data = items,
                sourceLoadStates = LoadStates(refresh = refresh, prepend = notLoading, append = notLoading),
            )
        )
    }
    return flow.collectAsLazyPagingItems()
}
