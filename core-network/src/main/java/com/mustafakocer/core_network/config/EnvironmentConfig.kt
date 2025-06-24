package com.mustafakocer.core_network.config
// # Dev/Staging/Prod configs
/**
 * Environment-specific configurations
 * Bu class her projede override edilebilir
 */

data class EnvironmentConfig(
    val baseUrl: String,
    val isDebug: Boolean = false,
    val enableLogging: Boolean = isDebug,
    val enableMockData: Boolean = false,
    val apiVersion: String = "v1",
    val userAgent: String = "MovieApp/1.0.0",
) {

    companion object {
        // Default configurations for different environments
        val DEVELOPMENT = EnvironmentConfig(
            baseUrl = "https://api.themoviedb.org/3/",
            isDebug = true,
            enableLogging = true,
            enableMockData = false
        )

        val STAGING = EnvironmentConfig(
            baseUrl = "https://staging-api.themoviedb.org/3/",
            isDebug = true,
            enableLogging = true,
            enableMockData = false
        )

        val PRODUCTION = EnvironmentConfig(
            baseUrl = "https://api.themoviedb.org/3/",
            isDebug = false,
            enableLogging = false,
            enableMockData = false
        )
    }
}