package com.mustafakocer.core_preferences.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.core_preferences.provider.DefaultSessionProvider
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.core_preferences.repository.ThemeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val APP_PREFERENCES_NAME = "movie_app_preferences"

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    // DataStore'u oluşturmak için context'e bir extension property ekliyoruz.
    // Bu, "name" parametresinin tek bir yerde tanımlanmasını sağlar.
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = APP_PREFERENCES_NAME
    )

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideThemeRepository(
        // Hilt, bu DataStore'u bir önceki adımda oluşturduğumuz
        // DataStoreModule'den alacağını biliyor.
        dataStore: DataStore<Preferences>,
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