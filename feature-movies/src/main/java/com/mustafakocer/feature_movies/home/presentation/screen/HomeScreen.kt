package com.mustafakocer.feature_movies.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.loading.LoadingScreen
import com.mustafakocer.feature_movies.home.presentation.components.FakeSearchBar
import com.mustafakocer.feature_movies.home.presentation.components.MovieCategorySection
import com.mustafakocer.feature_movies.home.presentation.contract.*
import com.mustafakocer.feature_movies.home.presentation.viewmodel.HomeViewModel
import com.mustafakocer.feature_movies.shared.domain.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    contract: UiContract<HomeUiState, HomeEvent, HomeEffect>,
) {
    val state by contract.uiState.collectAsState()
    val viewModel = contract as HomeViewModel

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("MovieApp") },
            actions = {
                IconButton(
                    onClick = { viewModel.navigateToProfile() }
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                }
            }
        )

        // Main content based on state
        Box(modifier = Modifier.fillMaxSize()) {
            when (state) {
                // Loading state
                is HomeUiState.Loading -> {
                    LoadingScreen(message = "Loading movies...")
                }

                // Error state
                is HomeUiState.Error -> {
                    ErrorScreen(
                        error = (state as HomeUiState.Error).errorInfo,
                        onRetry = { contract.onEvent(HomeEvent.RetryClicked) }
                    )
                }

                // Success state
                is HomeUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Fake Search Bar
                        FakeSearchBar(
                            onClick = { viewModel.navigateToSearch() }
                        )

                        // Movie Categories
                        (state as HomeUiState.Success).content.allSections.forEach { section ->
                            if (section.movies.isNotEmpty()) {
                                MovieCategorySection(
                                    section = section,
                                    isRetrying = (state as HomeUiState.Success).isCategoryRetrying(section.category),
                                    onMovieClick = { movie ->
                                        viewModel.navigateToMovieDetail(
                                            movieId = movie.id,
                                            movieTitle = movie.title
                                        )
                                    },
                                    onViewAllClick = {
                                        viewModel.navigateToMoviesList(section.category)
                                    },
                                    onRetryClick = {
                                        contract.onEvent(HomeEvent.RetryCategory(section.category))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}