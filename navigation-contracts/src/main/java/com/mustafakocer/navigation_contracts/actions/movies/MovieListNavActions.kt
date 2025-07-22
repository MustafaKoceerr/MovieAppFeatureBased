package com.mustafakocer.navigation_contracts.actions.movies

// MovieList screen'in ihtiyaç duyduğu eylemler
interface MovieListNavActions {
    fun navigateToMovieDetails(movieId: Int)
    fun navigateUp()
}