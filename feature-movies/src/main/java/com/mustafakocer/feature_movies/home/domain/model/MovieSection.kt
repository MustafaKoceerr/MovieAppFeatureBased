package com.mustafakocer.feature_movies.home.domain.model

import com.mustafakocer.feature_movies.shared.domain.model.Movie

data class MovieSection(
    val category: MovieCategoryType,
    val movies: List<Movie>,
    val isLoading: Boolean = false,
    val error: String? = null
){
    companion object{
        fun empty(category: MovieCategoryType) = MovieSection(category,emptyList())
        fun loading(category: MovieCategoryType) = MovieSection(category,emptyList(), isLoading = true)
        fun error(category: MovieCategoryType, error: String) = MovieSection(category, emptyList(), error = error)
        fun success(category: MovieCategoryType, movies: List<Movie>) = MovieSection(category, movies)
    }
}