package com.mustafakocer.core_preferences.models

/**
 * Defines the set of supported languages within the application, encapsulating all related properties.
 *
 * Architectural Note:
 * This enum acts as a single source of truth for language data. It consolidates properties
 * required by different layers:
 * - UI Layer: Uses `displayName` and `flagResourceName` for rendering language selection menus.
 * - Network Layer: Uses `apiParam` for localizing API requests.
 * This approach ensures consistency and simplifies adding or modifying languages in the future.
 *
 * @param code The ISO 639-1 language code (e.g., "en").
 * @param apiParam The specific parameter value required by the backend API (e.g., "en-US").
 * @param displayName The human-readable name for display in the UI.
 * @param flagResourceName The filename of the corresponding SVG flag resource in the assets.
 */
enum class LanguagePreference(
    val code: String,
    val apiParam: String,
    val displayName: String,
    val flagResourceName: String,
) {
    ENGLISH("en", "en-US", "English", "flag_us"),
    TURKISH("tr", "tr-TR", "Türkçe", "flag_tr"),
    FRENCH("fr", "fr-FR", "Français", "flag_fr"),
    GERMAN("de", "de-DE", "Deutsch", "flag_de"),
    SPANISH("es", "es-ES", "Español", "flag_es"),
    ITALIAN("it", "it-IT", "Italiano", "flag_it"),
    RUSSIAN("ru", "ru-RU", "Русский", "flag_ru");

    companion object {
        /** The default language preference for the application. */
        val DEFAULT = ENGLISH

        /**
         * Safely converts a string value to its corresponding [LanguagePreference].
         *
         * @param value The string to convert, typically retrieved from DataStore.
         * @return The matching [LanguagePreference], or [DEFAULT] if the value is null or invalid.
         */
        fun fromString(value: String?): LanguagePreference {
            return try {
                valueOf(value ?: DEFAULT.name)
            } catch (e: IllegalArgumentException) {
                // If the stored value is corrupted or no longer exists, fall back to default.
                DEFAULT
            }
        }

        /**
         * Provides a list of all available languages.
         * Useful for populating a language selection UI.
         */
        fun getAllLanguages(): List<LanguagePreference> {
            return entries
        }
    }
}