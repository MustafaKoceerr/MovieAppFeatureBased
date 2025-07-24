package com.mustafakocer.movieappfeaturebasedclean.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.mustafakocer.feature_auth.welcome.domain.handler.AuthCallbackHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * An invisible, transient Activity responsible for capturing the deep link callback from the
 * TMDB authentication flow. Its sole purpose is to parse the result from the incoming intent,
 * pass it to a central handler, and then immediately close itself. The user never sees this
 * Activity as part of the UI.
 *
 * ---
 *
 * ### Architectural Justification: The "Invisible Bridge" and the Single Activity Pattern
 *
 * A crucial question for architectural consistency is whether this approach violates the
 * Single Activity Pattern (SAP). The short answer is **no**; on the contrary, it supports and
 * strengthens it.
 *
 * **The Spirit of the Single Activity Pattern:**
 * The core philosophy of SAP is to manage the application's entire UI within a single host
 * Activity (e.g., `MainActivity`), using Fragments or, in our case, Jetpack Compose's
 * `NavController`. The goal is to avoid the complexities of the Android Activity lifecycle,
 * inter-intent data passing, and task stack management.
 *
 * **Why `AuthCallbackActivity` Aligns with SAP:**
 * This Activity is not a "screen" in the traditional sense and does not violate the pattern's spirit because:
 *
 * 1.  **It Has No UI (It's Invisible):** It has a transparent theme and does not call `setContent`.
 *     The user never perceives it as a screen. It is merely a background "handler."
 *
 * 2.  **It's Transient:** It calls `finish()` as soon as its work is done in `onCreate`. It never
 *     enters the application's back stack, and the user cannot navigate back to it.
 *
 * 3.  **It Has a Single Responsibility (The Bridge):** Its only job is to capture an event that
 *     originates outside the application (a deep link from the browser) and relay the result of
 *     that event back into the world of our Single Activity (via a shared handler). It is a
 *     bridge between the outside world and our app's internal, controlled environment.
 *
 * **An Analogy:**
 * Think of `MainActivity` as a large house containing all the rooms (composable screens).
 * `AuthCallbackActivity` is like a postman who receives a letter from outside (the deep link),
 * hands it through the mail slot, says "You've got mail," and then immediately disappears. The
 * postman is not a resident of the house and doesn't become part of its structure.
 *
 * **Why This is a Best Practice:**
 * This "invisible Activity bridge" is a clean, standard, and robust method for handling external
 * intents in a Single Activity architecture. It keeps `MainActivity` clean from the complexity
 * of handling various external event types. We avoid cluttering `MainActivity`'s `onNewIntent`
 * method with complex `if/else` blocks to determine if an intent is a deep link, a push
 * notification, or something else. Each external event type can be handled by its own small,
 *  focused, and disposable Activity.
 *
 * **Conclusion:**
 * This implementation does not break the Single Activity Pattern. Instead, by separating concerns
 * and keeping `MainActivity` clean, it allows for a more robust and maintainable application of the
 * pattern.
 */
@AndroidEntryPoint
class AuthCallbackActivity : ComponentActivity() {

    // The central handler, injected by Hilt, to pass the authentication result back to the
    // main application's logic. This is the core mechanism for bridging the result.
    @Inject
    lateinit var authCallbackHandler: AuthCallbackHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AuthCallbackActivity", "Activity created with intent: $intent")

        val uri = intent.data
        // Validate that the incoming intent is the one we expect.
        if (uri != null && uri.scheme == "movieapp" && uri.host == "auth") {
            val requestToken = uri.getQueryParameter("request_token")
            val isApproved = uri.getQueryParameter("approved") == "true"

            if (isApproved && requestToken != null) {
                // Happy path: Authentication was approved and we have a token.
                // On success, pass the token to the central handler.
                authCallbackHandler.onNewTokenReceived(requestToken)
            } else {
                // The user denied the request or an error occurred.
                // In a production app, we might notify the handler of the cancellation.
                // e.g., authCallbackHandler.onAuthCancelled()
            }
        } else {
//            Log.e("AuthCallbackActivity", "Invalid URI received for auth callback: $uri")
        }

        // Regardless of the outcome, the task of this Activity is complete. We bring the main
        // app to the front and immediately finish this one.
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            // This flag brings the existing MainActivity instance to the front instead of creating a new one.
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        startActivity(mainIntent)
        finish()
    }
}