package com.mustafakocer.movieappfeaturebasedclean.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mustafakocer.movieappfeaturebasedclean.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App database builder with pagination support
 *
 * SIMPLIFIED APPROACH:
 * ✅ Manual entity registration in AppDatabase
 * ✅ Room-compatible static configuration
 * ✅ Environment-specific configurations
 * ✅ Focus on working implementation first
 */
@Singleton
class AppDatabaseBuilder @Inject constructor() {

    /**
     * Build the main application database
     */
    fun buildDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = AppDatabase.DATABASE_NAME
        ).apply {
            configureDatabaseBuilder()
        }.build()
    }

    /**
     * Configure database builder with environment-specific settings
     */
    private fun RoomDatabase.Builder<AppDatabase>.configureDatabaseBuilder() {
        // Development configuration
        if (BuildConfig.DEBUG) {
            fallbackToDestructiveMigration()
            // Add debugging configurations
        } else {
            // Production configuration
            // addMigrations(*getAllMigrations())
        }

        // Common configurations
        setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        enableMultiInstanceInvalidation()

        // Add query callback for debugging in development
        if (BuildConfig.DEBUG) {
            setQueryCallback(
                { sqlQuery, bindArgs ->
                    println("SQL Query: $sqlQuery")
                    println("Bind Args: ${bindArgs.joinToString()}")
                },
                java.util.concurrent.Executors.newSingleThreadExecutor()
            )
        }
    }

    /**
     * Get simple database info for debugging
     */
    fun getDatabaseInfo(): DatabaseInfo {
        return DatabaseInfo(
            databaseName = AppDatabase.DATABASE_NAME,
            databaseVersion = AppDatabase.DATABASE_VERSION,
            entityCount = 2, // RemoteKey + MovieListEntity
            status = "Ready"
        )
    }

    data class DatabaseInfo(
        val databaseName: String,
        val databaseVersion: Int,
        val entityCount: Int,
        val status: String,
    )
}