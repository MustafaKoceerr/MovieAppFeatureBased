package com.mustafakocer.navigation_contracts.destinations

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations using @Serializable
 *
 * NETFLIX APPROACH:
 * ✅ Pure data classes with navigation parameters
 * ✅ Compile-time safety for navigation arguments
 * ✅ Automatic serialization/deserialization
 * ✅ No string-based route construction
 *
 * DESIGN PRINCIPLE:
 * - Each destination represents a screen with its required parameters
 * - Nested graphs group related destinations
 * - Helper functions for easy destination creation
 */

// ==================== ROOT NAVIGATION GRAPHS ====================

@Serializable
object AuthGraph

@Serializable
object MoviesGraph

// ==================== AUTH FEATURE DESTINATIONS ====================

@Serializable
object LoginDestination

@Serializable
object RegisterDestination

// ==================== MOVIES FEATURE DESTINATIONS ====================

@Serializable
object HomeDestination

@Serializable
object SearchDestination

@Serializable
object SettingsDestination

@Serializable
data class MovieDetailsDestination(
    val movieId: Int,
)

@Serializable
data class MoreMoviesDestination(
    val category: String,
    val categoryTitle: String,
)

// ==================== HELPER FUNCTIONS ====================

/**
 * Helper object for creating destinations with parameters
 *
 * USAGE:
 * - DestinationFactory.movieDetails(123, "Avatar")
 * - DestinationFactory.moreMovies("popular", "Popular Movies")
 */
object DestinationFactory {

    /**
     * Create MovieDetailsDestination with parameters
     */
    fun movieDetails(movieId: Int) =
        MovieDetailsDestination(movieId = movieId)

    /**
     * Create MoreMoviesDestination with parameters
     */
    fun moreMovies(category: String, title: String) =
        MoreMoviesDestination(category = category, categoryTitle = title)
}