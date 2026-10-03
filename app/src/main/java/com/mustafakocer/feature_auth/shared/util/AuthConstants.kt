package com.mustafakocer.feature_auth.shared.util

/**
 * Centralizes all constants related to the authentication flow with the TMDB service.
 *
 * Architectural Note:
 * Using a dedicated object for these constants prevents "magic strings" from being scattered
 * throughout the authentication code. It provides a single, reliable source for URLs and scheme
 * information, making the implementation cleaner and easier to maintain.
 */
object AuthConstants {
    /**
     * The base TMDB URL to which the user is redirected for web-based authentication.
     * A valid `request_token` must be appended to this URL.
     */
    const val TMDB_AUTHENTICATION_URL = "https://www.themoviedb.org/authenticate/"

    private const val REDIRECT_SCHEME = "movieapp"
    private const val REDIRECT_HOST = "auth"

    /**
     * The full deep link URL that the application listens for to capture the result of the
     * web authentication flow. This must match the `intent-filter` defined in the
     * `AndroidManifest.xml`.
     */
    const val REDIRECT_URL = "$REDIRECT_SCHEME://$REDIRECT_HOST/callback"
}