package com.mustafakocer.feature_movies.list.presentation.contract

import androidx.paging.PagingData
import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.feature_movies.list.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
/**
 * Movie List UI Contract
 *
 * CLEAN ARCHITECTURE: Presentation Layer - UI Contract
 * RESPONSIBILITY: Define UI communication patterns
 */

// ==================== UI STATE ====================

data class MovieListUiState(
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val categoryTitle: String = "",
    val categoryEndpoint: String = "",
    val isRefreshing: Boolean = false,
    val movies: Flow<PagingData<MovieListItem>>? = null,
) : BaseUiState