package com.mustafakocer.navigation_contracts

/**
 * Public API for navigation-contracts module
 *
 * NETFLIX APPROACH:
 * ✅ Single entry point for module consumers
 * ✅ Clear API surface
 * ✅ Easy imports for features
 *
 * USAGE:
 * import com.mustafakocer.navigation_contracts.*
 */

// Re-export specific classes/interfaces for easy consumption

// Destinations - Re-export factory and main destinations
typealias NavDestinationFactory = com.mustafakocer.navigation_contracts.destinations.DestinationFactory

// Actions - Re-export all action interfaces
typealias MoviesNavActions = com.mustafakocer.navigation_contracts.actions.MoviesNavigationActions
typealias AuthNavActions = com.mustafakocer.navigation_contracts.actions.AuthNavigationActions
typealias SearchNavActions = com.mustafakocer.navigation_contracts.actions.SearchNavigationActions
typealias SettingsNavActions = com.mustafakocer.navigation_contracts.actions.SettingsNavigationActions
typealias BaseNavActions = com.mustafakocer.navigation_contracts.actions.BaseNavigationActions
typealias CommonNavActions = com.mustafakocer.navigation_contracts.actions.CommonNavigationActions

// Events - Re-export event interfaces
typealias NavEvent = com.mustafakocer.navigation_contracts.events.NavigationEvent
typealias MoviesNavEvent = com.mustafakocer.navigation_contracts.events.NavigationEvent.MoviesNavigationEvent
typealias AuthNavEvent = com.mustafakocer.navigation_contracts.events.NavigationEvent.AuthNavigationEvent
typealias SearchNavEvent = com.mustafakocer.navigation_contracts.events.NavigationEvent.SearchNavigationEvent
typealias SettingsNavEvent = com.mustafakocer.navigation_contracts.events.NavigationEvent.SettingsNavigationEvent