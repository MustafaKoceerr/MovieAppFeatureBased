package com.mustafakocer.movieappfeaturebasedclean.di

import android.content.Context
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListRemoteKeyDao
import com.mustafakocer.movieappfeaturebasedclean.database.AppDatabase
import com.mustafakocer.movieappfeaturebasedclean.database.AppDatabaseBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides DAOs for all features and core database components
 *
 * Simplified approach - focus on getting Room working first
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseDaoModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        databaseBuilder: AppDatabaseBuilder,
    ): AppDatabase {
        return databaseBuilder.buildDatabase(context)
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(appDatabase: AppDatabase): androidx.room.RoomDatabase {
        return appDatabase
    }

    // ==================== CORE DAOS ====================

    @Provides
    @Singleton
    fun provideRemoteKeyDao(database: AppDatabase): com.mustafakocer.core_database.dao.RemoteKeyDao =
        database.remoteKeyDao()

    // ==================== FEATURE DAOS ====================

    @Provides
    @Singleton
    fun provideMovieListDao(database: AppDatabase): MovieListDao =
        database.movieListDao()

    @Provides
    @Singleton
    fun provideMovieListRemoteKeyDao(database: AppDatabase): MovieListRemoteKeyDao =
        database.movieListRemoteKeyDao()

    // Future DAOs can be added here as needed
}