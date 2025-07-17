package com.mustafakocer.navigation_contracts.actions

/**
 * :feature-movies modülünün tüm navigasyon eylemlerini tek bir yerde toplayan
 * birleşik sözleşme.
 */
interface FeatureMoviesNavActions : CommonNavActions {

    // Home, List, Search ekranlarından -> Detay ekranına
    fun navigateToMovieDetails(movieId: Int)

    // Home ekranından -> Daha fazla filmin olduğu liste ekranına
    fun navigateToMovieList(categoryEndpoint: String)

    // Home ekranından -> Arama ekranına
    fun navigateToSearch()

    // Home ekranından -> Ayarlar ekranına
    fun navigateToSettings()
}