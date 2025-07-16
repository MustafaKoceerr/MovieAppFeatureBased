package com.mustafakocer.feature_movies.home.domain.repository

import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    // ==================== MOVIE CATEGORIES ====================

    fun getNowPlayingMovies(page: Int = 1): Flow<List<MovieListItem>>

    fun getPopularMovies(page: Int = 1): Flow<List<MovieListItem>>

    fun getTopRatedMovies(page: Int = 1): Flow<List<MovieListItem>>

    fun getUpcomingMovies(page: Int = 1): Flow<List<MovieListItem>>

}