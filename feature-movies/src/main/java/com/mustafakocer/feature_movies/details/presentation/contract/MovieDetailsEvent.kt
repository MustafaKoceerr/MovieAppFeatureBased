package com.mustafakocer.feature_movies.details.presentation.contract

// ==================== EVENTS (User Actions) ====================

sealed class MovieDetailsEvent {

    /**
     * ✅ EVENT: User physically tapped retry button
     * - User action: Yes
     * - State change: Yes (→ Loading)
     * - Business logic: Yes (data fetching)
     * - Repeatable: Yes
     */
    object RetryLoading : MovieDetailsEvent()

    /**
     * ✅ EVENT: User physically tapped share button
     * - User action: Yes
     * - State change: Yes (isSharing = true)
     * - Business logic: Yes (prepare content)
     * - Repeatable: Yes
     */
    object ShareMovie : MovieDetailsEvent()

    /**
     * ✅ EVENT: User tapped dismiss on error dialog
     * - User action: Yes
     * - State change: Yes (Error → dismissed)
     * - Business logic: Maybe (should we navigate back?)
     * - Repeatable: Yes (in theory)
     */
    object DismissError : MovieDetailsEvent()

    /**
     * ✅ EVENT: User pressed back button
     * - User action: Yes
     * - State change: Maybe (could have conditions)
     * - Business logic: Maybe (unsaved changes?)
     * - Repeatable: Yes
     *
     * WHY NOT EFFECT? Because user decides to go back!
     * ViewModel might have logic: "Should I allow navigation?"
     */
    object BackPressed : MovieDetailsEvent()
}

