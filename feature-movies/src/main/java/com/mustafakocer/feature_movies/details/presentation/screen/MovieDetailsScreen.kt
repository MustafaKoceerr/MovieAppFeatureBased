// feature-movies/src/main/java/com/mustafakocer/feature_movies/details/presentation/screen/MovieDetailsScreen.kt
package com.mustafakocer.feature_movies.details.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.loading.LoadingScreen
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsUiState
import com.mustafakocer.feature_movies.details.util.fullBackdropUrl
import com.mustafakocer.feature_movies.details.util.fullPosterUrl

/**
 * TEACHING MOMENT: Network-Aware Movie Details Screen with Event-Driven Architecture
 *
 * CLEAN ARCHITECTURE PRINCIPLES:
 * ✅ UI Layer - Pure presentation logic
 * ✅ No business logic - delegates to ViewModel via events
 * ✅ Reactive UI - responds to state changes
 * ✅ Composable architecture - reusable components
 *
 * NETWORK-AWARE UX PATTERNS:
 * ✅ Data preservation during connectivity issues
 * ✅ Contextual error handling (snackbar vs full screen)
 * ✅ Manual refresh via toolbar button
 * ✅ Offline indicators in UI
 * ✅ Graceful degradation of functionality
 *
 * EVENT-DRIVEN INTERACTIONS:
 * ✅ All user interactions trigger events
 * ✅ No direct ViewModel method calls
 * ✅ Consistent interaction patterns
 * ✅ Testable user interactions
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    contract: UiContract<MovieDetailsUiState, MovieDetailsEvent, MovieDetailsEffect>,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val state by contract.uiState.collectAsStateWithLifecycle()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            MovieDetailsTopBar(
                title = state.movieDetailsOrNull?.title ?: "Movie Details",
                isOffline = when (state) {
                    is MovieDetailsUiState.Success -> (state as MovieDetailsUiState.Success).isOffline
                    is MovieDetailsUiState.SuccessWithNetworkError -> true
                    else -> false
                },
                onNavigateBack = {
                    // EVENT: User pressed back button
                    contract.onEvent(MovieDetailsEvent.BackPressed)
                },
                onRefresh = if (state.hasMovieDetails) {
                    {
                        // EVENT: User tapped refresh button
                        contract.onEvent(MovieDetailsEvent.RefreshDetails)
                    }
                } else null
            )
        },
        floatingActionButton = {
            // Show share FAB only when data is available
            if (state.hasMovieDetails) {
                FloatingActionButton(
                    onClick = {
                        // EVENT: User tapped share button
                        contract.onEvent(MovieDetailsEvent.ShareMovie)
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    if (state.isSharingInProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share movie",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        // Main content based on state
        when (state) {
            is MovieDetailsUiState.InitialLoading -> {
                LoadingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    message = "Loading movie details..."
                )
            }

            is MovieDetailsUiState.Success -> {
                MovieDetailsContent(
                    movieDetails = (state as MovieDetailsUiState.Success).movieDetails,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    isRefreshLoading = false,
                    isOffline = (state as MovieDetailsUiState.Success).isOffline
                )
            }

            is MovieDetailsUiState.SuccessWithNetworkError -> {
                // CORE PATTERN: Show data even with network errors
                MovieDetailsContent(
                    movieDetails = (state as MovieDetailsUiState.SuccessWithNetworkError).movieDetails,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    isRefreshLoading = false,
                    isOffline = true
                )
            }

            is MovieDetailsUiState.RefreshLoading -> {
                MovieDetailsContent(
                    movieDetails = (state as MovieDetailsUiState.RefreshLoading).movieDetails,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    isRefreshLoading = true,
                    isOffline = false
                )
            }

            is MovieDetailsUiState.Error -> {
                // CORE PATTERN: Show error screen only when no data exists
                ErrorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    error = (state as MovieDetailsUiState.Error).errorInfo,
                    onRetry = {
                        // EVENT: User tapped retry button
                        contract.onEvent(MovieDetailsEvent.RetryLoading)
                    },
                    onNavigateBack = {
                        // EVENT: User dismissed error
                        contract.onEvent(MovieDetailsEvent.DismissError)
                    }
                )
            }
        }
    }
}

/**
 * Top app bar with network status indicator
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MovieDetailsTopBar(
    title: String,
    isOffline: Boolean,
    onNavigateBack: () -> Unit,
    onRefresh: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Network status indicator
                if (isOffline) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "Offline",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back"
                )
            }
        },
        actions = {
            // Manual refresh button (only shown when data is available)
            onRefresh?.let { refresh ->
                IconButton(onClick = refresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

/**
 * Main movie details content with refresh loading overlay
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MovieDetailsContent(
    movieDetails: MovieDetails,
    modifier: Modifier = Modifier,
    isRefreshLoading: Boolean = false,
    isOffline: Boolean = false,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero section with backdrop and poster
            MovieHeroSection(
                movieDetails = movieDetails,
                isOffline = isOffline
            )

            // Movie stats (rating, release date, runtime)
            MovieStatsSection(movieDetails = movieDetails)

            // Genres
            if (movieDetails.genres.isNotEmpty()) {
                MovieGenresSection(genres = movieDetails.genres)
            }

            // Overview
            MovieOverviewSection(overview = movieDetails.overview)

            // Bottom spacer for FAB
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Refresh loading overlay
        if (isRefreshLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Refreshing...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * Hero section with backdrop image and poster
 */
@Composable
private fun MovieHeroSection(
    movieDetails: MovieDetails,
    isOffline: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Backdrop image
        AsyncImage(
            model = movieDetails.fullBackdropUrl(),
            contentDescription = "${movieDetails.title} backdrop",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Movie info overlay
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Poster
            AsyncImage(
                model = movieDetails.fullPosterUrl(),
                contentDescription = "${movieDetails.title} poster",
                modifier = Modifier.size(120.dp, 180.dp),
                contentScale = ContentScale.Crop
            )

            // Movie basic info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = movieDetails.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (movieDetails.hasTagline) {
                    Text(
                        text = "\"${movieDetails.tagline}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Offline indicator for content
                if (isOffline) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Offline content",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Offline content",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Movie statistics section (rating, date, runtime)
 */
@Composable
private fun MovieStatsSection(movieDetails: MovieDetails) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Rating
        MovieStatItem(
            icon = Icons.Default.Star,
            label = "Rating",
            value = String.format("%.1f", movieDetails.voteAverage),
            modifier = Modifier.weight(1f)
        )

        // Release Date
        MovieStatItem(
            icon = Icons.Default.DateRange,
            label = "Release",
            value = movieDetails.releaseDate,
            modifier = Modifier.weight(1f)
        )

        // Runtime (if available)
        if (movieDetails.runtime != null) {
            val hours = movieDetails.runtime / 60
            val minutes = movieDetails.runtime % 60
            val runtimeText = when {
                hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
                hours > 0 -> "${hours}h"
                else -> "${minutes}m"
            }

            MovieStatItem(
                icon = Icons.Default.Schedule,
                label = "Runtime",
                value = runtimeText,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual movie stat item
 */
@Composable
private fun MovieStatItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Movie genres section
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MovieGenresSection(genres: List<com.mustafakocer.feature_movies.details.domain.model.Genre>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Genres",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            genres.forEach { genre ->
                AssistChip(
                    onClick = { /* Could navigate to genre-based movies */ },
                    label = {
                        Text(
                            text = genre.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }
    }
}

/**
 * Movie overview section
 */
@Composable
private fun MovieOverviewSection(overview: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = overview,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
            )
        }
    }
}

/**
 * TEACHING MOMENT: Event-Driven UI Interactions
 *
 * PATTERN APPLIED:
 * ✅ All user interactions trigger events via contract.onEvent()
 * ✅ No direct ViewModel method calls
 * ✅ Consistent interaction patterns throughout the screen
 * ✅ Easy to test user interactions
 *
 * EXAMPLES:
 * - Back button: contract.onEvent(MovieDetailsEvent.BackPressed)
 * - Share button: contract.onEvent(MovieDetailsEvent.ShareMovie)
 * - Retry button: contract.onEvent(MovieDetailsEvent.RetryLoading)
 * - Manual refresh: contract.onEvent(MovieDetailsEvent.RefreshDetails)
 * - Dismiss error: contract.onEvent(MovieDetailsEvent.DismissError)
 *
 * REFRESH OPTIONS:
 * - Manual refresh via toolbar button (when data exists)
 * - Retry via error screen (when no data)
 * - Network snackbar retry (when offline with data)
 *
 * BENEFITS:
 * 1. TESTABILITY: Easy to test user interactions
 * 2. CONSISTENCY: Same pattern across all screens
 * 3. MAINTAINABILITY: Clear user action boundaries
 * 4. DEBUGGING: Easy to trace user actions
 * 5. FLEXIBILITY: ViewModel can handle business logic
 * 6. SIMPLICITY: No complex pull-to-refresh state management
 */