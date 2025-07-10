package com.mustafakocer.core_preferences.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


// DataStore delegate - creates DataStore lazily
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_preferences"
)

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    /**
     * Provide DataStore<Preferences> instance
     *
     * SINGLETON: Same instance across entire app
     * LAZY: Created only when first accessed
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return context.dataStore
    }
}