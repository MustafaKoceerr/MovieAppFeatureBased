package com.mustafakocer.navigation_contracts.actions.movies

/**
 * Defines the navigation actions that can be triggered from the Movie Details screen.
 *
 * Architectural Note:
 * This interface serves as a navigation contract, allowing the Movie Details feature module
 * to request navigation actions without being aware of the overall navigation structure.
 * The feature module depends on this contract, while the `:app` module provides the concrete
 * implementation, thus inverting the dependency and preserving module independence.
 */
interface MovieDetailsNavActions {

    fun navigateUp()
}