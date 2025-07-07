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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
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
import com.mustafakocer.core_ui.component.error.GenericErrorMessageFactory
import com.mustafakocer.core_ui.component.loading.LoadingScreen
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsUiState
import com.mustafakocer.feature_movies.details.presentation.viewmodel.MovieDetailsViewModel
import com.mustafakocer.feature_movies.details.util.fullBackdropUrl
import com.mustafakocer.feature_movies.details.util.fullPosterUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    contract: UiContract<MovieDetailsUiState, MovieDetailsEvent, MovieDetailsEffect>,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val state by contract.uiState.collectAsStateWithLifecycle()
    val viewModel = contract as MovieDetailsViewModel // cast to access navigation methods

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (state) {
                            is MovieDetailsUiState.Success -> (state as MovieDetailsUiState.Success).movieDetails.title
                            else -> "Movie Details"
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            if (state is MovieDetailsUiState.Success) {
                FloatingActionButton(
                    onClick = { viewModel.shareMovie() },
                    modifier = Modifier.padding(16.dp)
                ) {
                    if ((state as MovieDetailsUiState.Success).isSharing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share movie"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is MovieDetailsUiState.InitialLoading -> {
                    LoadingScreen(message = "Loading movie details...")
                }

                is MovieDetailsUiState.Success -> {
                    MovieDetailsContent(
                        movieDetails = (state as MovieDetailsUiState.Success).movieDetails,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is MovieDetailsUiState.Error -> {
                    ErrorScreen(
                        error = (state as MovieDetailsUiState.Error).errorInfo,
                        onRetry = { contract.onEvent(MovieDetailsEvent.RetryLoading) },
                        onNavigateBack = { viewModel.navigateBack() }
                    )
                }

                is MovieDetailsUiState.FullScreenError -> {
                    ErrorScreen(
                        error = GenericErrorMessageFactory.unknownError((state as MovieDetailsUiState.FullScreenError).message),
                        onRetry = if ((state as MovieDetailsUiState.FullScreenError).canRetry) {
                            { contract.onEvent(MovieDetailsEvent.RetryLoading) }
                        } else null,
                        onNavigateBack = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MovieDetailsContent(
    movieDetails: MovieDetails,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        // Movie Backdrop with Poster
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Backdrop Image
            AsyncImage(
                model = movieDetails.fullBackdropUrl(),
                contentDescription = "${movieDetails.title} backdrop",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
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

            // Movie Poster (Bottom Left)
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .size(width = 120.dp, height = 180.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                AsyncImage(
                    model = movieDetails.fullPosterUrl(),
                    contentDescription = "${movieDetails.title} poster",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Movie Information
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title and Tagline
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = movieDetails.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                if (movieDetails.hasTagline) {
                    Text(
                        text = "\"${movieDetails.tagline}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Movie Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
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

                // Runtime
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

            // Genres
            if (movieDetails.genres.isNotEmpty()) {
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
                        movieDetails.genres.forEach { genre ->
                            AssistChip(
                                onClick = { /* Could navigate to genre filter */ },
                                label = { Text(genre.name) }
                            )
                        }
                    }
                }
            }

            // Overview
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
                        text = movieDetails.overview,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
                    )
                }
            }

            // Bottom Spacer for FAB
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

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
 * TEACHING MOMENT: Why Separate Route and Screen?
 *
 * ROUTE RESPONSIBILITIES:
 * ✅ Handle navigation effects
 * ✅ Handle side effects (sharing, toasts)
 * ✅ Manage system interactions
 * ✅ Load initial data
 *
 * SCREEN RESPONSIBILITIES:
 * ✅ Pure UI rendering
 * ✅ State-driven UI logic
 * ✅ User interaction handling
 * ✅ Layout and styling
 *
 * BENEFITS:
 * ✅ Separation of concerns
 * ✅ Easier testing (Screen can be tested in isolation)
 * ✅ Reusable Screen component
 * ✅ Clear responsibility boundaries
 */

/**
 * TEACHING MOMENT: Movie Details Screen Implementation
 *
 * CLEAN ARCHITECTURE: Presentation Layer
 * RESPONSIBILITY: Display movie details with beautiful UI
 *
 * UI FEATURES:
 * ✅ Movie poster with backdrop
 * ✅ Movie info (title, rating, release date, runtime)
 * ✅ Genres as chips
 * ✅ Overview text
 * ✅ Share functionality
 * ✅ Loading and error states
 */