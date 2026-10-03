package com.mustafakocer.navigation_contracts.actions.movies

/**
 * Defines the navigation actions that can be triggered from the Settings screen.
 *
 * Architectural Note:
 * This interface serves as a navigation contract, enabling the Settings feature module to
 * request navigation actions without any knowledge of the application's overall navigation
 * graph. The feature module depends on this abstraction, and the `:app` module provides the
 * concrete implementation, thereby preserving module independence and adhering to the
 * Dependency Inversion Principle.
 */
interface SettingsNavActions {

    fun navigateUp()
}