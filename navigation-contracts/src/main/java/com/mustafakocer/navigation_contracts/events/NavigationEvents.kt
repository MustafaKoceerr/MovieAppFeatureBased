package com.mustafakocer.navigation_contracts.events

/**
 * Navigation events for event-driven navigation
 *
 * NETFLIX APPROACH:
 * ✅ Event-based navigation for complex flows
 * ✅ Decoupled navigation triggers
 * ✅ Easier testing with event verification
 * ✅ Clear navigation intent separation
 *
 * USAGE:
 * ViewModels emit these events, UI layer handles navigation
 */

sealed interface NavigationEvent {

    // ==================== MOVIES NAVIGATION EVENTS ====================

    sealed interface MoviesNavigationEvent : NavigationEvent {

        data class NavigateToMovieDetails(
            val movieId: Int,
            val movieTitle: String? = null
        ) : MoviesNavigationEvent

        data class NavigateToMoreMovies(
            val category: String,
            val categoryTitle: String
        ) : MoviesNavigationEvent

        object NavigateToSearch : MoviesNavigationEvent

        object NavigateToSettings : MoviesNavigationEvent

        object NavigateToHome : MoviesNavigationEvent

        object NavigateToAuth : MoviesNavigationEvent

        object NavigateBack : MoviesNavigationEvent
    }

    // ==================== AUTH NAVIGATION EVENTS ====================

    sealed interface AuthNavigationEvent : NavigationEvent {

        object NavigateToRegister : AuthNavigationEvent

        object NavigateToMainApp : AuthNavigationEvent

        object NavigateBack : AuthNavigationEvent
    }

    // ==================== SEARCH NAVIGATION EVENTS ====================

    sealed interface SearchNavigationEvent : NavigationEvent {

        data class NavigateToMovieDetails(
            val movieId: Int,
            val movieTitle: String? = null
        ) : SearchNavigationEvent

        object NavigateToHome : SearchNavigationEvent

        object NavigateBack : SearchNavigationEvent
    }

    // ==================== SETTINGS NAVIGATION EVENTS ====================

    sealed interface SettingsNavigationEvent : NavigationEvent {

        object NavigateToAuth : SettingsNavigationEvent

        object NavigateBack : SettingsNavigationEvent
    }
}