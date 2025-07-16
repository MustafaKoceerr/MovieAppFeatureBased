package com.mustafakocer.feature_movies.list.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEffect
import com.mustafakocer.feature_movies.list.presentation.viewmodel.MovieListViewModel
import com.mustafakocer.navigation_contracts.actions.FeatureMoviesNavActions
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MovieListRoute(
    navActions: FeatureMoviesNavActions,
    viewModel: MovieListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Yan etkileri (Effect'leri) dinleyip yöneten bölüm.
    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is MovieListEffect.NavigateToMovieDetail -> {
                    navActions.navigateToMovieDetails(effect.movieId)
                }
                is MovieListEffect.NavigateBack -> {
                    navActions.navigateUp()
                }
                is MovieListEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    // Saf UI bileşenini çağırıyoruz.
    MovieListScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}