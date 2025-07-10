package com.mustafakocer.feature_movies.details.presentation.screen

import android.content.Intent
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.viewmodel.MovieDetailsViewModel
import com.mustafakocer.navigation_contracts.DetailNavActions

@Composable
fun MovieDetailsRoute(
    navActions: DetailNavActions,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle UI Effects (side effects)
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MovieDetailsEffect.NavigateBack -> {
                    navActions.navigateBack()
                }

                is MovieDetailsEffect.ShareContent -> {
                    try {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, effect.title)
                            putExtra(Intent.EXTRA_TEXT, effect.content)
                        }

                        val chooserIntent = Intent.createChooser(shareIntent, effect.title)
                        context.startActivity(chooserIntent)
                    } catch (e: Exception) {
                        // ✅ ERROR: Show error snackbar if sharing fails
                        snackbarHostState.showSnackbar(
                            message = "Unable to share movie details",
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is MovieDetailsEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = if (effect.isError) SnackbarDuration.Long else SnackbarDuration.Short
                    )
                }

                is MovieDetailsEffect.ShowNetworkSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = if (effect.isOffline) "📱 ${effect.message}" else "📶 ${effect.message}",
                        actionLabel = if (effect.isOffline) "Retry" else null,
                        duration = SnackbarDuration.Long
                    )

                    viewModel.onEvent(MovieDetailsEvent.DismissNetworkSnackbar)
                }
            }
        }
    }

    MovieDetailsScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}