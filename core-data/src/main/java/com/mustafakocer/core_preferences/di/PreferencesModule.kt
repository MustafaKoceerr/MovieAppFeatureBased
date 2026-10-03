package com.mustafakocer.core_preferences.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.core_preferences.repository.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val APP_PREFERENCES_NAME = "movie_app_preferences"

/**
 * A Hilt module responsible for providing concrete instances related to data persistence
 * using Jetpack DataStore.
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = APP_PREFERENCES_NAME
    )

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideThemeRepository(dataStore: DataStore<Preferences>): ThemeRepository {
        return ThemeRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideLanguageRepository(dataStore: DataStore<Preferences>): LanguageRepository {
        return LanguageRepository(dataStore)
    }
}
