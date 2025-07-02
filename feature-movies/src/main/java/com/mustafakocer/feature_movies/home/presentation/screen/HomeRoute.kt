package com.mustafakocer.feature_movies.home.presentation.screen

import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustafakocer.feature_movies.home.presentation.contract.HomeEffect
import com.mustafakocer.feature_movies.home.presentation.viewmodel.HomeViewModel
import com.mustafakocer.navigation_contracts.MoviesNavActions

// HomeRoute.kt → uiEffect’i toplar ve navigation işlemlerini yapar
/**
 * HomeRoute with integrated navigation actions
 *
 * CHANGES:
 * ✅ Uses MoviesNavActions instead of NavController
 * ✅ Implements all navigation effects
 * ✅ Clean separation between navigation and UI
 */
@Composable
fun HomeRoute(
    navActions: MoviesNavActions,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Handle navigation effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToMovieDetail -> {
                    navActions.navigateToMovieDetails(
                        movieId = effect.route.movieId
                    )
                }

                is HomeEffect.NavigateToMoviesList -> {
                    navActions.navigateToMoreMovies(
                        category = effect.route.category,
                        categoryTitle = effect.route.title
                    )
                }

                is HomeEffect.NavigateToProfile -> {
                    // TODO: Implement when profile feature is ready
                    // For now, navigate to settings
                    navActions.navigateToSettings()
                }

                is HomeEffect.NavigateToSearch -> {
                    navActions.navigateToSearch()
                }

                is HomeEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = effect.actionLabel,
                        duration = SnackbarDuration.Short
                    )
                }

                is HomeEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    HomeScreen(contract = viewModel)
}