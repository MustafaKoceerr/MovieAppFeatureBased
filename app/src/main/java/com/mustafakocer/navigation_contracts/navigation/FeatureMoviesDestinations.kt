package com.mustafakocer.navigation_contracts.navigation

import kotlinx.serialization.Serializable

/**
 * Defines all type-safe navigation destinations within the `:feature_movies` module.
 */

/**
 * Represents the nested navigation graph for the entire movies feature.
 *
 * Architectural Note:
 * This object serves as the root route for the movies feature's subgraph. Using `@Serializable`
 * objects for navigation destinations is a core concept of modern, type-safe navigation libraries
 * in Compose. It provides compile-time safety, eliminating `String`-based routes and reducing
 * the risk of runtime errors. This graph object allows for encapsulating all movie-related
 * screens within a single, cohesive navigation flow.
 */
@Serializable
object MoviesFeatureGraph

/**
 * Represents the type-safe route for the main Home screen.
 */
@Serializable
object HomeScreen

/**
 * Represents the type-safe route for the screen that displays a list of movies for a given category.
 * @param categoryEndpoint The unique identifier for the movie category (e.g., "popular", "top_rated").
 */
@Serializable
data class MovieListScreen(val categoryEndpoint: String) {
    companion object {

        const val KEY_CATEGORY_ENDPOINT = "categoryEndpoint"
    }
}

/**
 * Represents the type-safe route for the screen that displays the details of a single movie.
 * @param movieId The unique identifier for the movie.
 */
@Serializable
data class MovieDetailsScreen(val movieId: Int) {
    companion object {

        const val KEY_MOVIE_ID = "movieId"
    }
}

/**
 * Represents the type-safe route for the movie Search screen.
 */
@Serializable
object SearchScreen

/**
 * Represents the type-safe route for the application Settings screen.
 */
@Serializable
object SettingsScreen