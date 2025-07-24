package com.mustafakocer.navigation_contracts.actions.auth

/**
 * Defines the navigation actions that can be triggered from the Account screen.
 *
 * Architectural Note:
 * This interface serves as a navigation contract, a key pattern for decoupling feature modules
 * from the main navigation graph. The `:feature_account` module owns and depends on this
 * contract, but the actual implementation is provided by the `:app` module, which has knowledge
 * of the complete navigation graph. This prevents the feature module from having a direct
 * dependency on other features or the main navigation component, promoting true modularization.
 */
interface AccountNavActions {

    fun navigateToWelcome()

    fun navigateUp()
}