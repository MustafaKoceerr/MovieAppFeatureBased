package com.mustafakocer.feature_movies.database

import com.mustafakocer.feature_movies.list.data.local.entity.MovieListEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Movies feature database info provider
 *
 * Simplified approach - no dynamic registration for now
 * Just holds entity information for manual registration
 */
@Singleton
class MoviesDatabaseInfo @Inject constructor() {

    /**
     * Get all movie-related entities for manual registration
     */
    fun getMovieEntities(): Array<Class<*>> {
        return arrayOf(
            MovieListEntity::class.java,
            // Add other movie-related entities here when needed
            // MovieDetailsEntity::class.java,
            // FavoriteMovieEntity::class.java,
        )
    }

    /**
     * Get movie-specific type converters
     */
    fun getMovieTypeConverters(): Array<Class<*>> {
        return arrayOf(
            // Add movie-specific type converters here when needed
            // MovieGenreConverter::class.java,
        )
    }

    /**
     * Get feature version for migration tracking
     */
    fun getFeatureVersion(): Int {
        return 1 // Increment when movie schema changes
    }

    /**
     * Get feature name for debugging
     */
    fun getFeatureName(): String {
        return "movies"
    }
}