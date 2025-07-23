package com.mustafakocer.core_android.util

import android.content.Context
import android.content.ContextWrapper
import java.util.Locale

/**
 * Creates a new Context with an updated Locale configuration.
 *
 * This utility is essential for dynamically changing the application's language at runtime.
 * It generates a new context with the specified locale, which can then be used to inflate
 * views and access resources in the chosen language.
 *
 * @param locale The new [Locale] to be set.
 * @return A [ContextWrapper] containing the new configuration.
 */
fun Context.updateLocale(locale: Locale): ContextWrapper {
    val configuration = this.resources.configuration
    configuration.setLocale(locale)
    val newContext = this.createConfigurationContext(configuration)
    return ContextWrapper(newContext)
}