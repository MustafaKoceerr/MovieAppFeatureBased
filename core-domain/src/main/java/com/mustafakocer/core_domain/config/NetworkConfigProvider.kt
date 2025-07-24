package com.mustafakocer.core_domain.config

/**
 * Provides centralized network configuration properties for the entire application.
 *
 * Why this is an interface in the domain layer:
 * This interface follows the Dependency Inversion Principle. The 'core_domain' module defines the
 * required contract, while the actual implementation (which holds concrete values) is provided

 * in a higher-level module (e.g., the 'app' module). This decouples the domain logic from
 * specific build configurations or environments, making the architecture cleaner and more modular.
 */
interface NetworkConfigProvider {
    val baseUrl: String

    val apiKey: String

    val userAgent: String

    val isDebug: Boolean

    val enableLogging: Boolean
}
