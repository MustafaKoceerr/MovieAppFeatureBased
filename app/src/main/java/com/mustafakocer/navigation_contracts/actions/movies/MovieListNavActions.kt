package com.mustafakocer.navigation_contracts.actions.movies

/**
 * Defines the navigation actions that can be triggered from the Movie List screen.
 *
 * Architectural Note:
 * This interface is a navigation contract that decouples the Movie List feature module from
 * the concrete implementation of the navigation graph. The feature's ViewModel can request
 * navigation to a movie's details or to go back, but the `:app` module is responsible for
 * implementing the logic. This maintains module isolation and prevents circular dependencies.
 */
interface MovieListNavActions {

    fun navigateToMovieDetails(movieId: Int)

    fun navigateUp()
}