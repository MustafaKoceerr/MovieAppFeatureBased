package com.mustafakocer.navigation_contracts.navigation

import kotlinx.serialization.Serializable

/**
 * Defines all type-safe navigation destinations within the `:feature_splash` module.
 */

/**
 * Represents the nested navigation graph for the entire splash feature.
 *
 * Architectural Note:
 * This object serves as the root route for the splash feature's subgraph. Using `@Serializable`
 * objects for navigation destinations is a core concept of modern, type-safe navigation libraries
 * in Compose. It provides compile-time safety, eliminating `String`-based routes and reducing
 * the risk of runtime errors.
 */
@Serializable
object SplashFeatureGraph

/**
 * Represents the type-safe route for the application's initial Splash screen.
 */
@Serializable
object SplashScreen