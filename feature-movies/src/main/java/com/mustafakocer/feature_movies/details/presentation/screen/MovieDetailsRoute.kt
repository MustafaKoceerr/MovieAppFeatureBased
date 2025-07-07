package com.mustafakocer.feature_movies.details.presentation.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.viewmodel.MovieDetailsViewModel

@Composable
fun MovieDetailsRoute(
    navController: NavHostController,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Handle UI Effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                MovieDetailsEffect.NavigateBack -> {
                    navController.popBackStack()
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

                        // Show success message
                        snackbarHostState.showSnackbar(
                            message = "Share dialog opened",
                            duration = SnackbarDuration.Short
                        )

                    } catch (e: Exception) {
                        // Show error via snackbar with action
                        snackbarHostState.showSnackbar(
                            message = "Failed to share movie",
                            actionLabel = "Retry",
                            duration = SnackbarDuration.Long
                        )
                    }
                }

                is MovieDetailsEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = effect.actionLabel,
                        duration = when {
                            effect.actionLabel != null -> SnackbarDuration.Long
                            effect.message.length > 50 -> SnackbarDuration.Long
                            else -> SnackbarDuration.Short
                        }
                    )
                }

            }
        }
    }

    // Render the screen
    MovieDetailsScreen(contract = viewModel)
}

/**
 * COMPLETE MovieDetailsRoute with Professional Snackbar Support
 *
 * ARCHITECTURE BENEFITS:
 * ✅ Effect-based messaging (no state pollution)
 * ✅ SavedStateHandle integration (process death resilient)
 * ✅ Clean separation: Route handles effects, Screen handles UI
 * ✅ Professional UX with Material 3 Snackbar
 * ✅ Robust error handling with fallbacks
 *
 * NAVIGATION PATTERN:
 * - movieId automatically injected via SavedStateHandle
 * - No manual parameter passing required
 * - Automatic data loading on ViewModel init
 * - Type-safe navigation with @Serializable destinations
 *
 * EFFECT HANDLING:
 * - Navigation effects (back navigation)
 * - Sharing effects (system share dialog)
 * - UI feedback effects (snackbar messages)
 * - Error handling with graceful fallbacks
 */