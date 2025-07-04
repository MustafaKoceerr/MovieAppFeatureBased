package com.mustafakocer.feature_movies.di

import com.mustafakocer.database_contracts.FeatureDatabaseContributor
import com.mustafakocer.feature_movies.database.MoviesDatabaseContributor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet



/**
 * Movies feature database DI module
 *
 * DATABASE CONTRACTS PATTERN:
 * ✅ Registers feature database contributor
 * ✅ Uses Dagger multibinding for modular registration
 * ✅ App module will collect all contributors
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MoviesDatabaseModule {

    /**
     * Contribute movies database entities to main database
     * Uses @IntoSet for multibinding - app module will collect all
     */
    @Binds
    @IntoSet
    abstract fun bindMoviesDatabaseContributor(
        contributor: MoviesDatabaseContributor
    ): FeatureDatabaseContributor
}