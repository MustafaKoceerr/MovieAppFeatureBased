package com.mustafakocer.feature_movies.home.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.feature_movies.home.domain.model.MovieCategoryType

// Events
sealed interface HomeEvent : BaseUiEvent {
    // Data loading events
    object LoadInitialData : HomeEvent
    object RefreshAllCategories : HomeEvent
    object RetryClicked : HomeEvent
    data class RetryCategory(val categoryType: MovieCategoryType) : HomeEvent
}