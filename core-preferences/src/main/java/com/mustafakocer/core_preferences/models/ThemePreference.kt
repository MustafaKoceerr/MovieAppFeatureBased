package com.mustafakocer.core_preferences.models

/**
 * Theme preference options
 *
 * KISS PRINCIPLE: Only 3 essential options
 * App-agnostic: No Android UI dependencies
 */
enum class ThemePreference {
    LIGHT,
    DARK,
    SYSTEM;

    /**
     * Get display name for UI
     * Simple string - no resource dependencies
     */
    val displayName: String
        get() = when (this) {
            LIGHT -> "Light"
            DARK -> "Dark"
            SYSTEM -> "System"
        }

    companion object {
        /**
         * Default theme preference
         */
        val DEFAULT = SYSTEM

        /**
         * Get preference from string safely
         */
        fun fromString(value: String?): ThemePreference {
            return try {
                valueOf(value ?: DEFAULT.name)
            } catch (e: IllegalArgumentException) {
                DEFAULT
            }
        }
    }
}