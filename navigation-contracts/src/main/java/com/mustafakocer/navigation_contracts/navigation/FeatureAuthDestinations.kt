package com.mustafakocer.navigation_contracts.navigation

import kotlinx.serialization.Serializable

/**
 * Defines all type-safe navigation destinations within the `:feature_auth` module.
 */

/**
 * Represents the nested navigation graph for the entire authentication feature.
 *
 * Architectural Note:
 * This object serves as the root route for the auth feature's subgraph. Using `@Serializable`
 * objects for navigation destinations is a core concept of modern, type-safe navigation libraries
 * in Compose. It provides compile-time safety, eliminating `String`-based routes and reducing
 * the risk of runtime errors. This graph object allows for encapsulating all auth-related
 * screens (like Welcome and Account) within a single, cohesive navigation flow.
 */
@Serializable
object AuthFeatureGraph

/**
 * Represents the type-safe route for the Welcome screen, which handles user login.
 */
@Serializable
object WelcomeScreen

/**
 * Represents the type-safe route for the Account screen, where users can view their details or log out.
 */
@Serializable
object AccountScreen