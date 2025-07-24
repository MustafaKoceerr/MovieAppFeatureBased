package com.mustafakocer.feature_movies.home.presentation.screen

import HomeScreenSkeleton
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.error.toErrorInfo
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.home.presentation.components.FakeSearchBar
import com.mustafakocer.feature_movies.home.presentation.components.MovieCategorySection
import com.mustafakocer.feature_movies.home.presentation.contract.*
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory

/**
 * A purely visual, "dumb" component that displays the UI for the Home screen.
 *
 * @param state The current UI state to render.
 * @param onEvent A lambda to propagate user interactions up to the ViewModel.
 * @param snackbarHostState The state manager for displaying Snackbars.
 *
 * Architectural Note:
 * This Composable is responsible for the overall structure of the screen using `Scaffold`.
 * It uses `Crossfade` to smoothly transition between full-screen loading/error states and
 * the main content. The `PullToRefreshBox` is integrated here to provide the pull-to-refresh
 * functionality, wrapping the main content when data is available. This component is stateless
 * and is driven entirely by its inputs, making it easy to preview and test.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(
                        onClick = { onEvent(HomeEvent.AccountClicked) },
                        modifier = Modifier.bounceClick()
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = stringResource(R.string.account)
                        )
                    }
                    IconButton(
                        onClick = { onEvent(HomeEvent.SettingsClicked) },
                        modifier = Modifier.bounceClick()
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Crossfade(
                targetState = state.showFullScreenLoading || state.showFullScreenError,
                animationSpec = tween(500),
                label = "ContentCrossfade"
            ) { showLoadingOrError ->
                if (showLoadingOrError) {
                    if (state.showFullScreenError) {
                        ErrorScreen(
                            error = state.error!!.toErrorInfo(),
                            onRetry = { onEvent(HomeEvent.Refresh) }
                        )
                    } else {
                        HomeScreenSkeleton()
                    }
                } else {
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { onEvent(HomeEvent.Refresh) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        HomeContent(state = state, onEvent = onEvent)
                    }
                }
            }
        }
    }
}

/**
 * Displays the main content of the Home screen, including search bar and movie categories.
 */
@Composable
private fun HomeContent(state: HomeUiState, onEvent: (HomeEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        FakeSearchBar(onClick = { onEvent(HomeEvent.SearchClicked) })

        Spacer(modifier = Modifier.height(16.dp))

        // Iterate through movie categories and display them.
        // The order is determined by the enum's declaration order.
        state.categories.forEach { (category, movies) ->
            MovieCategorySection(
                categoryTitle = category.toLocalizedTitle(),
                movies = movies,
                onMovieClick = { movieId -> onEvent(HomeEvent.MovieClicked(movieId)) },
                onViewAllClick = { onEvent(HomeEvent.ViewAllClicked(category)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * An extension function to provide a localized title for each [MovieCategory].
 */
@Composable
fun MovieCategory.toLocalizedTitle(): String {
    return when (this) {
        MovieCategory.NOW_PLAYING -> stringResource(id = R.string.category_title_now_playing)
        MovieCategory.POPULAR -> stringResource(id = R.string.category_title_popular)
        MovieCategory.TOP_RATED -> stringResource(id = R.string.category_title_top_rated)
        MovieCategory.UPCOMING -> stringResource(id = R.string.category_title_upcoming)
    }
}