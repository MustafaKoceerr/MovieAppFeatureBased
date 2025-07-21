package com.mustafakocer.feature_auth.welcome.util

object AuthConstants {
    /**
     * Kullanıcıyı kimlik doğrulaması için yönlendireceğimiz temel TMDB URL'si.
     * Sonuna request_token eklenmelidir.
     */
    const val TMDB_AUTHENTICATION_URL = "https://www.themoviedb.org/authenticate/"

    // YENİ SABİTLER
    private const val REDIRECT_SCHEME = "movieapp"
    private const val REDIRECT_HOST = "auth"
    const val REDIRECT_URL = "$REDIRECT_SCHEME://$REDIRECT_HOST/callback"
}