package com.mustafakocer.navigation_contracts.actions.movies

// Search screen'in ihtiyaç duyduğu eylemler
interface SearchNavActions {
    fun navigateToMovieDetails(movieId: Int)
    fun navigateUp()
}