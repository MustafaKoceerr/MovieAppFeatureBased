package com.mustafakocer.navigation_contracts.navigation

import kotlinx.serialization.Serializable

// :feature-movies modülüne ait tüm hedefler

@Serializable
object MoviesFeatureGraph // Bu, tüm feature'ın kök rotası olacak

@Serializable
object HomeScreen

@Serializable
data class MovieListScreen(val categoryEndpoint: String) {
    // Bu companion object, argüman anahtarını merkezi ve tip-güvenli
    // bir şekilde saklamamızı sağlar.
    companion object {
        const val KEY_CATEGORY_ENDPOINT = "categoryEndpoint"
        // parametrelerin adlarını böyle const val'lara tanımlıyoruz.
        // Bu şekilde ViewModel içinde savedStateHandle'da bu parametreler yerine bize gönderilen parametreleri alabiliyoruz.
    }
}

@Serializable
data class MovieDetailsScreen(val movieId: Int) {
    companion object {
        const val KEY_MOVIE_ID = "movieId"
    }
}

@Serializable
object SearchScreen

@Serializable
object SettingsScreen
