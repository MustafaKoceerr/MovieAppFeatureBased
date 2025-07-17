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
    ENGLISH("en", "en-US", "English", "flag_us"),
    TURKISH("tr", "tr-TR", "Türkçe", "flag_tr"),
    FRENCH("fr", "fr-FR", "Français", "flag_fr"),     // <-- YENİ
    GERMAN("de", "de-DE", "Deutsch", "flag_de"),       // <-- YENİ
    SPANISH("es", "es-ES", "Español", "flag_es"),      // <-- YENİ
    ITALIAN("it", "it-IT", "Italiano", "flag_it"),     // <-- YENİ
    RUSSIAN("ru", "ru-RU", "Русский", "flag_ru");      // <-- YENİ

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