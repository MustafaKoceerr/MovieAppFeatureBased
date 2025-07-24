package com.mustafakocer.movieappfeaturebasedclean.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.feature_movies.home.data.local.dao.HomeMovieDao
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.movieappfeaturebasedclean.data.database.AppDatabase
import com.mustafakocer.movieappfeaturebasedclean.data.database.DatabaseConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * A Dagger Hilt module responsible for providing all database-related dependencies for the application.
 *
 * This module centralizes the creation of the main `AppDatabase` instance and exposes the individual
 * Data Access Objects (DAOs) so they can be injected into repositories across different feature modules.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the singleton instance of the application's main [AppDatabase].
     *
     * Architectural Decision: This function is the single source for creating the database. It uses
     * the `Room.databaseBuilder` to construct the database instance. By centralizing its creation
     * here, we ensure that the entire application shares the same database instance, which is critical
     * for data consistency.
     *
     * @param context The application context, provided by Hilt.
     * @return The singleton [AppDatabase] instance.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(appDatabase: AppDatabase): RoomDatabase {
        return appDatabase
    }

    @Provides
    @Singleton
    fun provideMovieListDao(appDatabase: AppDatabase): MovieListDao {
        return appDatabase.movieListDao()
    }

    @Provides
    @Singleton
    fun provideRemoteKeyDao(appDatabase: AppDatabase): RemoteKeyDao {
        return appDatabase.remoteKeyDao()
    }

    @Provides
    @Singleton
    fun provideHomeMovieDao(appDatabase: AppDatabase): HomeMovieDao {
        return appDatabase.homeMovieDao()
    }
}