package com.mustafakocer.core_network.config
// # Timeouts, base URLs
/**
 * TEACHING MOMENT: Configuration Management
 *
 * ✅ Centralized network configuration
 * ✅ Environment-specific settings
 * ✅ Easily testable timeouts
 * ✅ Production-ready defaults
 */
object NetworkConfig {

    // Timeouts (in seconds)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Cache settings
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB
    const val CACHE_MAX_AGE = 60 // 1 minute
    const val CACHE_MAX_STALE = 60 * 60 * 24 * 7 // 1 week

    // Retry settings
    const val MAX_RETRY_ATTEMPTS = 3
    const val RETRY_DELAY_MS = 1000L
    const val BACKOFF_MULTIPLIER = 2.0

    // Request limits
    const val MAX_REQUESTS_PER_HOST = 5
    const val MAX_IDLE_CONNECTIONS = 5
    const val KEEP_ALIVE_DURATION = 5L // minutes
}
