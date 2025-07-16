package com.mustafakocer.feature_movies.home.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_movies.home.presentation.contract.HomeEffect
import com.mustafakocer.feature_movies.home.presentation.viewmodel.HomeViewModel
import com.mustafakocer.navigation_contracts.actions.FeatureMoviesNavActions
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeRoute(
    navActions: FeatureMoviesNavActions,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Yan etkileri (Effect'leri) dinleyip yöneten bölüm.
    // Bu blok, HomeRoute ekranda olduğu sürece aktif kalır.
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is HomeEffect.NavigateToMovieDetails -> navActions.navigateToMovieDetails(effect.movieId)
                is HomeEffect.NavigateToMovieList -> navActions.navigateToMovieList(
                    effect.categoryTitle,
                    effect.categoryEndpoint
                )

                is HomeEffect.NavigateToSettings -> navActions.navigateToSettings()
                is HomeEffect.NavigateToSearch -> navActions.navigateToSearch()
                is HomeEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        // actionLabel = "Kapat" // İsteğe bağlı
                    )
                }
            }
        }
    }


    // Saf UI bileşenini çağırıyoruz.
    // State'i ve event handler'ı paslıyoruz.
    HomeScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}