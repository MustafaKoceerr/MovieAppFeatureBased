package com.mustafakocer.movieappfeaturebasedclean.config

import com.mustafakocer.core_common.config.NetworkConfigProvider
import com.mustafakocer.movieappfeaturebasedclean.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MovieNetworkConfigProvider @Inject constructor() : NetworkConfigProvider {
    override val baseUrl = BuildConfig.API_URL
    override val apiKey = BuildConfig.API_KEY
    override val isDebug = BuildConfig.ENABLE_LOGGING
    override val enableLogging = BuildConfig.ENABLE_LOGGING
    override val userAgent = "${BuildConfig.APP_NAME}/${BuildConfig.VERSION_NAME}"
}