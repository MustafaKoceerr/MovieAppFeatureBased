package com.mustafakocer.navigation_contracts.actions.movies

/**
 * Defines the navigation actions that can be triggered from the Search screen.
 *
 * Architectural Note:
 * This interface is a navigation contract that allows the Search feature module to request
 * navigation events without having a direct dependency on the main navigation graph or other
 * feature modules. The `:app` module implements this contract, inverting the dependency
 * and preserving the modularity of the search feature.
 */
interface SearchNavActions {

    fun navigateToMovieDetails(movieId: Int)

    fun navigateUp()
}