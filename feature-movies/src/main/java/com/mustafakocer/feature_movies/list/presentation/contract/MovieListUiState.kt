package com.mustafakocer.feature_movies.list.presentation.contract

import androidx.paging.PagingData
import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.feature_movies.shared.domain.model.Movie
import com.mustafakocer.feature_movies.shared.domain.model.MovieList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

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
    val movies: Flow<PagingData<MovieList>> = emptyFlow(),
) : BaseUiState