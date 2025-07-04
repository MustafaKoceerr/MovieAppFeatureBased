package com.mustafakocer.movieappfeaturebasedclean.database

import com.mustafakocer.database_contracts.DatabaseEntityRegistry
import com.mustafakocer.database_contracts.FeatureDatabaseContributor
import com.mustafakocer.feature_movies.database.MoviesDatabaseContributor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App database registry implementation
 *
 * DATABASE CONTRACTS PATTERN:
 * ✅ Implements DatabaseEntityRegistry interface
 * ✅ Collects entities from all feature contributors
 * ✅ Centralized database composition
 * ✅ Version management across features
 */
@Singleton
class AppDatabaseRegistry @Inject constructor(
    private val featureContributors: Set<@JvmSuppressWildcards FeatureDatabaseContributor> // ✅ Bu annotation eksik
) : DatabaseEntityRegistry {
    override fun getEntityClasses(): Array<Class<*>> {
        val allEntities = mutableListOf<Class<*>>()

        // Add core database entities
        allEntities.addAll(getCoreEntityClasses())

        // Add entities from all feature contributors
        featureContributors.forEach { contributor ->
            allEntities.addAll(contributor.provideEntityClasses())
        }
        return allEntities.toTypedArray()
    }

    override fun getDatabaseVersion(): Int {
        // Calculate version based on feature versions
        var baseVersion = 1
        val featureVersionSum = featureContributors.sumOf { it.getFeatureVersion() }
        return baseVersion + featureVersionSum
    }

    override fun getTypeConverters(): Array<Class<*>> {
        val allConverters = mutableListOf<Class<*>>()

        // add core converters
        allConverters.addAll(getCoreTypeConverters())

        // Add converters from all feature contributors
        featureContributors.forEach { contributor ->
            allConverters.addAll(contributor.provideTypeConverters())
        }
        return allConverters.toTypedArray()
    }

    /**
     * Get feature contributors for debugging
     */
    fun getFeatureContributors(): Map<String, FeatureDatabaseContributor> {
        return featureContributors.associateBy { it.getFeatureName() }
    }

    /**
     * Validate all contributors are properly registered
     */
    fun validateContributors(): List<String> {
        val issues = mutableListOf<String>()

        featureContributors.forEach { contributor ->
            // Check if feature name is not empty
            if (contributor.getFeatureName().isBlank()) {
                issues.add("Feature contributor has empty name")
            }

            // Check if entities are provided
            if (contributor.provideEntityClasses().isEmpty()) {
                issues.add("Feature '${contributor.getFeatureName()}' provides no entities")
            }

            // Check version is positive
            if (contributor.getFeatureVersion() < 1) {
                issues.add("Feature '${contributor.getFeatureName()}' has invalid version: ${contributor.getFeatureVersion()}")
            }
        }
        return issues
    }

    private fun getCoreEntityClasses(): List<Class<*>> {
        return listOf(
            com.mustafakocer.core_database.pagination.RemoteKey::class.java,
            // Add other core entities here
        )
    }

    private fun getCoreTypeConverters(): List<Class<*>>{
        return listOf(
            // Add core type converters here
            // com.mustafakocer.core_database.converters.DateConverter::class.java,
        )
    }
}