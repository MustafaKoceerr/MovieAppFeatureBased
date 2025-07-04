package com.mustafakocer.feature_movies.database

import com.mustafakocer.database_contracts.FeatureDatabaseContributor
import com.mustafakocer.feature_movies.list.data.local.entity.MovieListEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Movies feature database contributor
 *
 * DATABASE CONTRACTS PATTERN:
 * ✅ Implements FeatureDatabaseContributor interface
 * ✅ Registers feature entities to main database
 * ✅ Provides type converters and migration info
 */
@Singleton
class MoviesDatabaseContributor @Inject constructor() : FeatureDatabaseContributor {
    override fun provideEntityClasses(): Array<Class<*>> {
        return arrayOf(
            MovieListEntity::class.java,
            // Add other movie-related entities here
            // MovieDetailsEntity::class.java,
            // FavoriteMovieEntity::class.java,
        )

    }

    override fun provideTypeConverters(): Array<Class<*>> {
        return arrayOf(
            // Add movie-specific type converters here
            // MovieGenreConverter::class.java,
            // MovieImageConverter::class.java,
        )
    }

    override fun getFeatureVersion(): Int {
        return 1 // Increment when movie schema changes
    }

    override fun getFeatureName(): String {
        return "movies"
    }

}