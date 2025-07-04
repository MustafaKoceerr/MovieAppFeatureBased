package com.mustafakocer.database_contracts.registry

/**
 * Entity registry contract for database composition
 *
 * DATABASE CONTRACTS PATTERN:
 * Feature registration system
 * Centralized entity management
 * Type-safe registration
 */
interface DatabaseEntityRegistry {
    /**
     * Get all registered entity classes
     */
    fun getEntityClasses(): Array<Class<*>>

    /**
     * Get database version
     */
    fun getDatabaseVersion(): Int

    /**
     * Get type converters
     */
    fun getTypeConverters(): Array<Class<*>>
}