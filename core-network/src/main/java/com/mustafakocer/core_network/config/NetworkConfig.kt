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
    const val CONNECT_TIMEOUT = 10L
    const val READ_TIMEOUT = 15L
    const val WRITE_TIMEOUT = 15L

    // ==================== CACHE SETTINGS ====================
    const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB

}
