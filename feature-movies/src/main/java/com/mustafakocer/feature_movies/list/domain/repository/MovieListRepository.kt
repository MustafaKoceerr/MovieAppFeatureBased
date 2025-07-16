package com.mustafakocer.feature_movies.list.domain.repository

import androidx.paging.PagingData
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow

interface MovieListRepository {

    /**
     * Get paginated movies for specific category
     */
    fun getMoviesForCategory(category: MovieCategory): Flow<PagingData<MovieListItem>>

    /**
     * Refresh cache for specific category
     */
    suspend fun refreshCategory(category: MovieCategory)
}