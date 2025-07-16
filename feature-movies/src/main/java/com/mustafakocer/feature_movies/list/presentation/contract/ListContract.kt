package com.mustafakocer.feature_movies.list.presentation.contract

import androidx.paging.PagingData
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// ==================== STATE ====================
data class MovieListUiState(
    // Paging 3'ün kendisi bir Flow olduğu için, onu state içinde tutuyoruz.
    val movies: Flow<PagingData<MovieListItem>> = emptyFlow(),

    // TopAppBar'da gösterilecek olan kategori başlığı.
    val categoryTitle: String = "",

    // BaseUiState'den gelen bu alanları, Paging dışındaki işlemler için
    // (ileride eklenebilecek) tutarlılık adına koruyoruz.
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: AppException? = null
) : BaseUiState

// ==================== EVENT ====================
sealed interface MovieListEvent : BaseUiEvent {
    data class MovieClicked(val movieId: Int) : MovieListEvent
    object BackClicked : MovieListEvent
}

// ==================== EFFECT ====================
sealed interface MovieListEffect : BaseUiEffect {
    data class NavigateToMovieDetail(val movieId: Int) : MovieListEffect
    object NavigateBack : MovieListEffect
    data class ShowSnackbar(val message: String) : MovieListEffect
}