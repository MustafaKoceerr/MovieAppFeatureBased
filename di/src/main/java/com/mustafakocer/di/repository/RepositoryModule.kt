package com.mustafakocer.di.repository


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.core_preferences.repository.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideThemeRepository(
        // Hilt, bu DataStore'u bir önceki adımda oluşturduğumuz
        // DataStoreModule'den alacağını biliyor.
        dataStore: DataStore<Preferences>
    ): ThemeRepository {
        return ThemeRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideLanguageRepository(dataStore: DataStore<Preferences>): LanguageRepository {
        return LanguageRepository(dataStore)
    }
    // Gelecekte eklenecek diğer ortak repository'ler de buraya gelecek...
}