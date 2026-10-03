package com.mustafakocer.core_preferences.models

/**
 * Defines the available theme options for the application.
 *
 * Architectural Note:
 * This enum serves as the single source of truth for theme settings. It is intentionally kept
 * simple and free of Android framework dependencies, allowing it to be used across different
 * layers (e.g., in domain logic or data repositories) without coupling them to the UI.
 */
enum class ThemePreference {
    LIGHT,
    DARK,
    SYSTEM;

    val displayName: String
        get() = when (this) {
            LIGHT -> "Light"
            DARK -> "Dark"
            SYSTEM -> "System"
        }

    companion object {

        val DEFAULT = SYSTEM

        fun fromString(value: String?): ThemePreference {
            return try {
                valueOf(value ?: DEFAULT.name)
            } catch (e: IllegalArgumentException) {
                DEFAULT
            }
        }
    }
}