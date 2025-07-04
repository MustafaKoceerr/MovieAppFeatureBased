package com.mustafakocer.database_contracts.registry

/**
 * Feature database contributor contract
 * Each future implements this to contribute to main database
 */
interface FeatureDatabaseContributor {
    /**
     * Provide entity classes for this feature
     */
    fun provideEntityClasses(): Array<Class<*>>

    /**
     * Provide type converters for this feature
     */
    fun provideTypeConverters(): Array<Class<*>>

    /**
     * Feature version for migration tracking
     */
    fun getFeatureVersion(): Int

    /**
     * Feature name identifier
     */
    fun getFeatureName(): String
}