package com.mustafakocer.movieappfeaturebasedclean.database

import com.mustafakocer.feature_movies.database.MoviesDatabaseInfo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simplified app database registry
 *
 * MANUAL APPROACH:
 * ✅ Simple info collection from features
 * ✅ No complex dynamic registration
 * ✅ Focus on getting basic functionality working
 * ✅ Easy to debug and understand
 */
@Singleton
class AppDatabaseRegistry @Inject constructor(
    private val moviesDatabaseInfo: MoviesDatabaseInfo
) {

    /**
     * Get all feature database info for debugging
     */
    fun getFeatureInfo(): Map<String, Any> {
        return mapOf(
            "movies" to mapOf(
                "featureName" to moviesDatabaseInfo.getFeatureName(),
                "featureVersion" to moviesDatabaseInfo.getFeatureVersion(),
                "entityCount" to moviesDatabaseInfo.getMovieEntities().size,
                "converterCount" to moviesDatabaseInfo.getMovieTypeConverters().size
            )
        )
    }

    /**
     * Get database statistics for monitoring
     */
    fun getDatabaseStats(): DatabaseStats {
        return DatabaseStats(
            totalFeatures = 1, // Only movies for now
            totalEntities = 1 + moviesDatabaseInfo.getMovieEntities().size, // Core + Movies
            totalConverters = moviesDatabaseInfo.getMovieTypeConverters().size,
            registrationApproach = "Manual"
        )
    }

    data class DatabaseStats(
        val totalFeatures: Int,
        val totalEntities: Int,
        val totalConverters: Int,
        val registrationApproach: String
    )
}