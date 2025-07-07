package com.mustafakocer.core_network.config
// # Timeouts, base URLs
/**
 * SIMPLIFIED: Network configuration constants
 *
 * KISS PRINCIPLE: Only keeps actually used configurations
 * ✅ Timeouts - USED in NetworkModule
 * ✅ Cache settings - NOW USED in NetworkModule
 * ✅ Connection limits - NOW USED in NetworkModule
 * ✅ Retry settings - WILL BE USED in RetryStrategy
 */
object NetworkConfig {

    // ==================== TIMEOUTS ====================
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // ==================== CACHE SETTINGS ====================
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB
    const val CACHE_MAX_AGE = 60 // 1 minute
    const val CACHE_MAX_STALE = 60 * 60 * 24 * 7 // 1 week

    // ==================== CONNECTION LIMITS ====================
    const val MAX_REQUESTS_PER_HOST = 5
    const val MAX_IDLE_CONNECTIONS = 5
    const val KEEP_ALIVE_DURATION = 5L // minutes

    // ==================== RETRY SETTINGS ====================
    const val MAX_RETRY_ATTEMPTS = 3
    const val RETRY_DELAY_MS = 1000L
    const val BACKOFF_MULTIPLIER = 2.0
}
