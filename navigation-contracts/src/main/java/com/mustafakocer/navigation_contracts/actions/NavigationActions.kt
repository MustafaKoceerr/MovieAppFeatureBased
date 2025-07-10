package com.mustafakocer.navigation_contracts.actions

/**
 * Navigation action contracts for features
 *
 * NETFLIX APPROACH:
 * ✅ Interface segregation - features only get what they need
 * ✅ Testable contracts - easy to mock for testing
 * ✅ Decoupled navigation - features don't know about destinations
 * ✅ Clear responsibilities - each interface has specific purpose
 *
 * PRINCIPLE:
 * Features depend on these interfaces, app module implements them
 */

// ==================== BASE NAVIGATION ====================

interface BaseNavigationActions {
    /**
     * Navigate back to previous screen
     */
    fun navigateBack()
}

// ==================== COMMON NAVIGATION ====================

interface CommonNavigationActions : BaseNavigationActions {
    /**
     * Navigate to home screen and clear backstack
     */
    fun navigateToHome()

    /**
     * Navigate to authentication (logout)
     */
    fun navigateToAuth()
}

// ==================== MOVIES FEATURE ACTIONS ====================

interface MoviesNavigationActions : BaseNavigationActions {
    /**
     * Navigate to movie details screen
     */
    fun navigateToMovieDetails(movieId: Int)

    /**
     * Navigate to more movies of specific category
     */
    fun navigateToMoreMovies(category: String, categoryTitle: String)

    /**
     * Navigate to search screen
     */
    fun navigateToSearch()

    /**
     * Navigate to settings screen
     */
    fun navigateToSettings()

    /**
     * Navigate to home screen and clear backstack
     */
    fun navigateToHome()

    /**
     * Navigate to authentication (logout)
     */
    fun navigateToAuth()
}

// ==================== AUTH FEATURE ACTIONS ====================

interface AuthNavigationActions : BaseNavigationActions {
    /**
     * Navigate to register screen
     */
    fun navigateToRegister()

    /**
     * Navigate to main app after successful login
     */
    fun navigateToMainApp()
}

// ==================== SEARCH FEATURE ACTIONS ====================

interface SearchNavigationActions : BaseNavigationActions {
    /**
     * Navigate to movie details from search
     */
    fun navigateToMovieDetails(movieId: Int)

    /**
     * Navigate back to home
     */
    fun navigateToHome()
}

// ==================== SETTINGS FEATURE ACTIONS ====================

interface SettingsNavigationActions : BaseNavigationActions {
    /**
     * Navigate to authentication (logout)
     */
    fun navigateToAuth()
}

// ==================== MOVIE LIST FEATURE ACTIONS ====================

interface MovieListNavigationActions : BaseNavigationActions {
    /**
     * Navigate to movie details screen
     */
    fun navigateToMovieDetails(movieId: Int)
}

interface DetailNavigationActions : BaseNavigationActions {
    /**
     * Sadece back işlemi var, o da Base navigation'dan geliyor.
     */
}
