package com.mustafakocer.core_preferences.models

/**
 * Language preference options with API parameters
 *
 * PATTERN: Same as ThemePreference
 * FEATURES: SVG flag support, API integration ready
 */
enum class LanguagePreference(
    val code: String,
    val apiParam: String,
    val displayName: String,
    val flagResource: String, // SVG dosya adı
) {
    ENGLISH(
        code = "en",
        apiParam = "en-US",
        displayName = "English",
        flagResource = "flag_us" // flag_us.svg
    ),
    TURKISH(
        code = "tr",
        apiParam = "tr-TR",
        displayName = "Türkçe",
        flagResource = "flag_tr" // flag_tr.svg
    );

    companion object {
        /**
         * Default language preference
         */
        val DEFAULT = ENGLISH

        /**
         * Get preference from string safely
         */
        fun fromString(value: String?): LanguagePreference {
            return try {
                valueOf(value ?: DEFAULT.name)
            } catch (e: IllegalArgumentException) {
                DEFAULT
            }
        }

        /**
         * Get all available languages for UI
         */
        fun getAllLanguages(): List<LanguagePreference> {
            return entries
        }
    }
}