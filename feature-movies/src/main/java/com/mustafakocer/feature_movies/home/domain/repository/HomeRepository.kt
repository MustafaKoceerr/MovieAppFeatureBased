package com.mustafakocer.feature_movies.home.domain.repository

import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    // ==================== MOVIE CATEGORIES ====================
    fun getNowPlayingMovies(page: Int = 1): Flow<Resource<List<MovieListItem>>>
    fun getPopularMovies(page: Int = 1): Flow<Resource<List<MovieListItem>>>
    fun getTopRatedMovies(page: Int = 1): Flow<Resource<List<MovieListItem>>>
    fun getUpcomingMovies(page: Int = 1): Flow<Resource<List<MovieListItem>>>
}