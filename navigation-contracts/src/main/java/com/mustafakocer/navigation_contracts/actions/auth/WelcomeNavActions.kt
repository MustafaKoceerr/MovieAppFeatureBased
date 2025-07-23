package com.mustafakocer.navigation_contracts.actions.auth

/**
 * Defines the navigation actions that can be triggered from the Welcome screen.
 *
 * Architectural Note:
 * This interface acts as a navigation contract, decoupling the Welcome screen (likely in the
 * `:feature_auth` module) from the main application's navigation graph. The feature module
 * calls the methods defined here, but the implementation is provided by the `:app` module.
 * This allows the Welcome screen to request navigation to the home screen without having a
 * direct dependency on the home feature, which is essential for true module isolation.
 */
interface WelcomeNavActions {

    fun navigateToHome()
}