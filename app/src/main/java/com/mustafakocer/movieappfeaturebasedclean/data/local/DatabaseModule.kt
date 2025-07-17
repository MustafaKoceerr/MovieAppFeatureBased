package com.mustafakocer.movieappfeaturebasedclean.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.feature_movies.home.data.repository.local.HomeMovieDao
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Tüm feature modüllerinden gelen katkıları kullanarak
     * dinamik bir AppDatabase implementasyonu sağlar.
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
            // .addMigrations(...) // Gerekirse migration'lar burada eklenir
            // TODO: Product için bunu kesinlikle kaldır, her seferinde database'yi yeniden oluşturuyor.
            .fallbackToDestructiveMigration() // Geliştirme aşaması için kullanışlı
            .build()
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(appDatabase: AppDatabase): RoomDatabase {
        return appDatabase
    }

    /**
     * AppDatabase içindeki DAO'ları tek tek sağlar.
     * Hilt, bu sayede Repository'lere sadece ihtiyaç duydukları DAO'yu enjekte edebilir.
     */
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