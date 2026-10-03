package com.mustafakocer.core_database.cache

/**
 * Provides standardized, human-readable constants for cache expiration durations.
 *
 * Architectural Note:
 * Centralizing these values in a dedicated object prevents the use of "magic numbers"
 * throughout the data layer. It ensures that caching policies are consistent and can be
 * easily managed from a single location.
 */
object CacheDuration {
    const val ONE_HOUR_MS = 60 * 60 * 1000L

    const val TWENTY_FOUR_HOURS_MS = 24 * ONE_HOUR_MS
}