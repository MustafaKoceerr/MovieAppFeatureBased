package com.mustafakocer.movieappfeaturebasedclean.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.mustafakocer.core_preferences.models.LanguagePreference

/**
 * Applies [language] as the app's UI language.
 *
 * Uses AppCompat's per-app locale support: on Android 13+ it delegates to the system, below that
 * AppCompat stores the choice itself (see `autoStoreLocales` in the manifest) and re-applies it
 * on every start. Visible activities are recreated automatically when the locale actually changes,
 * and calling it with the current language is a no-op.
 */
fun applyAppLanguage(language: LanguagePreference) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.code))
}
