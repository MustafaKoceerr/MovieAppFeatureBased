package com.mustafakocer.data_common.di

import dagger.hilt.components.SingletonComponent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mustafakocer.data_common.preferences.repository.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesRepositoryModule {

    /**
     * Provide ThemeRepository with DataStore dependency
     *
     * DataStore comes from core-preferences module
     */
    @Provides
    @Singleton
    fun provideThemeRepository(
        dataStore: DataStore<Preferences>, // Injected from core-preferences
    ): ThemeRepository {
        return ThemeRepository(dataStore)
    }

    // Future repositories will be added when needed:
    // @Provides
    // @Singleton
    // fun provideLanguageRepository(dataStore: DataStore<Preferences>) =
    //     LanguageRepository(dataStore)
}