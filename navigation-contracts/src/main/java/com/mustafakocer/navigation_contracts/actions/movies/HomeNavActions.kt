package com.mustafakocer.navigation_contracts.actions.movies

// Home screen'in ihtiyaç duyduğu eylemler
interface HomeNavActions {
    fun navigateToMovieDetails(movieId: Int)
    fun navigateToMovieList(categoryEndpoint: String)
    fun navigateToSearch()
    fun navigateToSettings()
    fun navigateToAccount()
}
