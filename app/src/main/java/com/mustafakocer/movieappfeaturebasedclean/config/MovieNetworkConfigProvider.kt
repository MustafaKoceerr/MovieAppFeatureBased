package com.mustafakocer.movieappfeaturebasedclean.config

import android.content.Context
import com.mustafakocer.core_domain.config.NetworkConfigProvider
import com.mustafakocer.movieappfeaturebasedclean.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Provides the application-specific network configuration values.
 *
 * Architectural Decision: This class serves as the concrete implementation of the abstract
 * `NetworkConfigProvider` interface defined in the `core-domain` layer. It acts as a bridge,
 * supplying the `core-network` module with the specific configuration values it needs (like API
 * keys and base URLs) without the core module needing to know where these values come from.
 * This is a key principle of dependency inversion and modular architecture.
 *
 * The configuration values are sourced from the `BuildConfig` file, which is the standard Android
 * practice for injecting environment-specific variables at compile time. This allows for different
 * configurations for various build types (e.g., debug, release, staging).
 *
 * @param context The application context, injected by Hilt, used to determine the cache directory.
 */
@Singleton
class MovieNetworkConfigProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : NetworkConfigProvider {
    override val baseUrl = BuildConfig.API_URL
    override val apiKey = BuildConfig.API_KEY
    override val isDebug = BuildConfig.ENABLE_LOGGING
    override val enableLogging = BuildConfig.ENABLE_LOGGING
    override val userAgent = "${BuildConfig.APP_NAME}/${BuildConfig.VERSION_NAME}"
}