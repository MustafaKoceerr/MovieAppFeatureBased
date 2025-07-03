package com.mustafakocer.feature_movies.details.presentation.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.viewmodel.MovieDetailsViewModel

@Composable
fun MovieDetailsRoute(
    movieId: Int,
    navController: NavHostController,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // Handle UI Effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                // ==================== NAVIGATION EFFECTS ====================
                MovieDetailsEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                // ==================== SHARING EFFECTS ====================

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
                        Toast.makeText(
                            context,
                            "Failed to share movie",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                // ==================== UI FEEDBACK EFFECTS ====================
                is MovieDetailsEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is MovieDetailsEffect.ShowSnackbar -> {
                    // TODO: Implement snackbar with SnackbarHost
                    // For now, show as toast
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Load movie details when route is first composed
    LaunchedEffect(movieId) {
        viewModel.onEvent(MovieDetailsEvent.LoadMovieDetails(movieId))
    }

    // Render the screen
    MovieDetailsScreen(contract = viewModel)
}