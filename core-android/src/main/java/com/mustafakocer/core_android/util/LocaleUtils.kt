package com.mustafakocer.core_android.util

import android.content.Context
import android.content.ContextWrapper
import java.util.Locale

fun Context.updateLocale(locale: Locale): ContextWrapper {
    val configuration = this.resources.configuration
    configuration.setLocale(locale)
    val newContext = this.createConfigurationContext(configuration)
    return ContextWrapper(newContext)
}