package com.mustafakocer.movieappfeaturebasedclean.config

import android.content.Context
import com.mustafakocer.core_common.config.NetworkConfigProvider
import com.mustafakocer.movieappfeaturebasedclean.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UPDATED: App-specific network configuration provider
 *
 * ✅ Provides app-specific network settings
 * ✅ Removes generic configs (moved to NetworkConfig)
 * ✅ Adds cache directory for OkHttpClient
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

    /**
     * Provide cache directory for OkHttpClient
     */
    fun getCacheDirectory(): File {
        return File(context.cacheDir, "http_cache")
    }

}