package com.mustafakocer.navigation_contracts.actions.splash

/**
 * Defines the navigation actions that can be triggered from the Splash screen.
 *
 * Architectural Note:
 * This interface is a critical navigation contract for the application's entry point. The
 * Splash screen's primary role is to determine the user's authentication state and then
 * delegate the navigation decision. This contract allows the Splash feature module to request
 * navigation to either the home or welcome screen without having a direct dependency on the
 * modules that contain them. The `:app` module implements this interface, providing the
 * concrete navigation logic and thus preserving module isolation.
 */
interface SplashNavActions {

    fun navigateToHome()

    fun navigateToWelcome()
}