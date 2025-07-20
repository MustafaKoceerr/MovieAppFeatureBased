package com.mustafakocer.feature_movies.home.domain.repository

import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getMoviesForCategory(category: MovieCategory): Flow<Resource<List<MovieListItem>>>
}