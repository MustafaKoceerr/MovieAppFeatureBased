package com.mustafakocer.di.preferences

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

private const val APP_PREFERENCES_NAME = "movie_app_preferences"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    // DataStore'u oluşturmak için context'e bir extension property ekliyoruz.
    // Bu, "name" parametresinin tek bir yerde tanımlanmasını sağlar.
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = APP_PREFERENCES_NAME
    )

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }
}