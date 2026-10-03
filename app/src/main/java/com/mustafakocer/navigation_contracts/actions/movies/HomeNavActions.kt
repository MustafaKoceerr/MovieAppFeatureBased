package com.mustafakocer.navigation_contracts.actions.movies

/**
 * Defines the navigation actions that can be triggered from the Home screen.
 *
 * Architectural Note:
 * This interface is a navigation contract that decouples the `:feature_home` module from the
 * concrete destinations of other features (like details, search, or settings). The Home
 * screen's ViewModel will invoke these methods, but the actual navigation logic is implemented
 * in the main `:app` module, which has a complete view of the navigation graph. This is crucial
 * for maintaining modularity and preventing circular dependencies between feature modules.
 */
interface HomeNavActions {

    fun navigateToMovieDetails(movieId: Int)

    fun navigateToMovieList(categoryEndpoint: String)

    fun navigateToSearch()

    fun navigateToSettings()

    fun navigateToAccount()
}