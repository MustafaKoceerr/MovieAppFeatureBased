package com.mustafakocer.core_network.config

/**
 * Provides centralized and static network configuration constants for the application.
 *
 * Architectural Note:
 * Using a singleton object (`object`) centralizes all magic numbers related to network
 * operations (like timeouts and cache sizes). This improves maintainability and ensures
 * consistency across all network-related components, such as the OkHttpClient setup.
 * It follows the KISS principle by only including configurations that are actively used.
 */
object NetworkConfig {

    const val CONNECT_TIMEOUT_SECONDS = 10L

    const val READ_TIMEOUT_SECONDS = 15L

    const val WRITE_TIMEOUT_SECONDS = 15L

    const val CACHE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB
}