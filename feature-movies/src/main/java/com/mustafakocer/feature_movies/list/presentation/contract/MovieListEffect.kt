package com.mustafakocer.feature_movies.list.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.feature_movies.home.presentation.contract.MovieDetailRoute

// ==================== UI EFFECTS ====================

sealed interface MovieListEffect : BaseUiEffect {
    data class NavigateToMovieDetail(val route: MovieDetailRoute) : MovieListEffect
    object NavigateBack : MovieListEffect
    data class ShowToast(val message: String) : MovieListEffect
    data class ShowError(val message: String) : MovieListEffect
}