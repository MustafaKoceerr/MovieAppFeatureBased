package com.mustafakocer.feature_movies.list.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.core_ui.component.error.ErrorCard
import com.mustafakocer.feature_movies.MovieConstants
import com.mustafakocer.feature_movies.list.domain.model.MovieListItem
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEffect
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEvent
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListUiState
import com.mustafakocer.feature_movies.list.presentation.viewmodel.MovieListViewModel

/**
 * Movie List Route
 *
 * RESPONSIBILITY: Handle navigation effects and side effects
 * PATTERN: Route handles effects, Screen handles pure UI
 */
@Composable
fun MovieListRoute(
    categoryEndpoint: String,
    categoryTitle: String,
    navController: NavHostController,
    viewModel: MovieListViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // Handle UI Effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                // ==================== NAVIGATION EFFECTS ====================
                is MovieListEffect.NavigateToMovieDetail -> {
                    // Navigate to movie detail screen
                    // navController.navigate("movie_detail/${effect.route.movieId}")
                }

                MovieListEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                // ==================== UI FEEDBACK EFFECTS ====================
                is MovieListEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is MovieListEffect.ShowError -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Initialize screen with parameters
    LaunchedEffect(categoryEndpoint, categoryTitle) {
        viewModel.onEvent(
            MovieListEvent.InitializeScreen(
                categoryEndpoint = categoryEndpoint,
                categoryTitle = categoryTitle
            )
        )
    }

    // Render the screen
    MovieListScreen(contract = viewModel)
}

/**
 * Movie List Screen
 *
 * CLEAN ARCHITECTURE: Presentation Layer - Pure UI
 * RESPONSIBILITY: Display paginated movie list with Material 3 design
 *
 * FEATURES:
 * - Lazy pagination with Paging3
 * - Pull-to-refresh
 * - Loading states
 * - Error handling
 * - Material 3 design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    contract: UiContract<MovieListUiState, MovieListEvent, MovieListEffect>,
) {
    val state by contract.uiState.collectAsStateWithLifecycle()
    val viewModel = contract as MovieListViewModel // Cast to access navigation methods

    // Remove pull to refresh for now - Material 3 API complexity
    // Can be added later with proper SwipeRefresh or custom implementation

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = state.categoryTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { contract.onEvent(MovieListEvent.RefreshMovies) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Movies list content
            state.movies?.let { moviesFlow ->
                val movies = moviesFlow.collectAsLazyPagingItems()

                MovieListContent(
                    movies = movies,
                    onMovieClick = { movie ->
                        contract.onEvent(
                            MovieListEvent.MovieClicked(
                                movieId = movie.id,
                                movieTitle = movie.title
                            )
                        )
                    },
                    onRetryClick = {
                        contract.onEvent(MovieListEvent.RetryClicked)
                    }
                )
            }

            // Refresh indicator
            if (state.isRefreshing) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

/**
 * Movie list content with pagination
 */
@Composable
private fun MovieListContent(
    movies: LazyPagingItems<MovieListItem>,
    onMovieClick: (MovieListItem) -> Unit,
    onRetryClick: () -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = movies.itemCount,
            key = movies.itemKey { it.id }
        ) { index ->
            val movie = movies[index]
            if (movie != null) {
                MovieListItem(
                    movie = movie,
                    onClick = { onMovieClick(movie) }
                )
            }
        }
        // ✅ PAGING3 BUILT-IN ERROR HANDLING

        // Handle refresh state (initial load)
        when (movies.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Loading movies...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is LoadState.Error -> {
                val error = movies.loadState.refresh as LoadState.Error
                item {
                    // ✅ FULL SCREEN ERROR WITH PAGING3 RETRY!
                    ErrorCard(
                        title = "Failed to Load Movies",
                        message = error.error.localizedMessage
                            ?: "An error occurred while loading movies",
                        onRetry = { movies.retry() } // ✅ PAGING3 MAGIC!
                    )
                }
            }

            else -> { /* Success - show items */
            }
        }

        // Handle append state (loading more pages)
        when (movies.loadState.append) {
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Loading more movies...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is LoadState.Error -> {
                val error = movies.loadState.append as LoadState.Error
                item {
                    // ✅ PAGINATION ERROR WITH PAGING3 RETRY!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        onClick = { movies.retry() } // ✅ PAGING3 MAGIC!
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Failed to load more",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = error.error.localizedMessage ?: "Tap to retry",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "🔄 Tap to Retry",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            else -> { /* Success or NotLoading */
            }
        }
    }
}

/**
 * Individual movie item
 */
@Composable
private fun MovieListItem(
    movie: MovieListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Movie poster
            AsyncImage(
                model = "${MovieConstants.IMAGE_BASE_URL}${MovieConstants.POSTER_SIZE}${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .size(80.dp, 120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Movie details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = movie.releaseDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ ${String.format("%.1f", movie.voteAverage)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "(${movie.voteCount} votes)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Retry button for error states
 */
@Composable
private fun RetryButton(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.padding(16.dp),
        onClick = onRetryClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Retry",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}