package com.mustafakocer.feature_movies.home.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiEffect

sealed interface HomeEffect : BaseUiEffect {
    // Navigation effects (type-safe)!
    data class NavigateToMovieDetail(val route: MovieDetailRoute) : HomeEffect
    data class NavigateToMoviesList(val route: MoviesListRoute) : HomeEffect
    object NavigateToSearch : HomeEffect
    object NavigateToProfile : HomeEffect

    // Ui Effects
    data class ShowToast(val message: String) : HomeEffect
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : HomeEffect
}