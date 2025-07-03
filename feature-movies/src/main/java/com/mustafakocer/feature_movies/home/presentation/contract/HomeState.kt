package com.mustafakocer.feature_movies.home.presentation.contract

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.core_ui.component.error.ErrorInfo
import com.mustafakocer.feature_movies.home.domain.model.HomeContent
import com.mustafakocer.feature_movies.home.domain.model.MovieCategoryType
import kotlinx.serialization.Serializable

/**
 * TEACHING MOMENT: Updated HomeContract for Multiple Categories
 */

//// State
//data class HomeState(
//    override val isLoading: Boolean = false,
//    override val error: String? = null,
//    val homeContent: HomeContent = HomeContent.empty(),
//    val isRefreshing: Boolean = false
//) : BaseUiState

sealed interface HomeUiState {
    object Loading : HomeUiState

    data class Error(
        val exception: AppException,
        val errorInfo: ErrorInfo,
    ) : HomeUiState

    data class Success(
        val content: HomeContent = HomeContent.empty(),
        val isRefreshing: Boolean = false,
        val retryingCategories: Set<MovieCategoryType> = emptySet(),
    ) : HomeUiState {
        fun isCategoryRetrying(categoryType: MovieCategoryType): Boolean {
            return retryingCategories.contains(categoryType)
        }
    }
}

// TODO Buradaki ekranları navigation modülünden al
// Route definitions
@Serializable
data class MovieDetailRoute(
    val movieId: Int,
    val movieTitle: String? = null,
)

@Serializable
data class MoviesListRoute(
    val category: String,
    val title: String,
)


