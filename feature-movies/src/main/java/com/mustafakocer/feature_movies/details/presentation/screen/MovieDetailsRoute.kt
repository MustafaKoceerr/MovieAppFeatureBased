package com.mustafakocer.feature_movies.details.presentation.screen

import android.content.Intent
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.viewmodel.MovieDetailsViewModel

/**
 * TEACHING MOMENT: Enhanced Route with Event-Driven Architecture and Network-Aware Effects
 *
 * ARCHITECTURE PATTERN: Route-Screen Separation with Event System
 * ✅ Route handles side effects (navigation, system interactions)
 * ✅ Screen handles pure UI rendering and event emission
 * ✅ Clear separation of concerns
 * ✅ Testable components (Screen can be tested in isolation)
 *
 * NETWORK-AWARE EFFECT HANDLING:
 * ✅ Network snackbars with appropriate styling and actions
 * ✅ Retry actions for connectivity issues
 * ✅ Contextual feedback based on network state
 * ✅ System integrations (sharing, navigation)
 *
 * EVENT-DRIVEN ENHANCEMENTS:
 * ✅ Effects are properly handled without direct ViewModel calls
 * ✅ Network snackbar dismissal triggers events
 * ✅ Retry actions properly integrated
 * ✅ Consistent event flow throughout the app
 */

@Composable
fun MovieDetailsRoute(
    movieId: Int,
    navController: NavHostController,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle UI Effects (side effects)
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                // ==================== NAVIGATION EFFECTS ====================

                is MovieDetailsEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                // ==================== SYSTEM INTEGRATION EFFECTS ====================

                is MovieDetailsEffect.ShareContent -> {
                    try {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, effect.title)
                            putExtra(Intent.EXTRA_TEXT, effect.content)
                        }

                        val chooserIntent = Intent.createChooser(shareIntent, effect.title)
                        context.startActivity(chooserIntent)

                    } catch (e: Exception) {
                        // Fallback: Show error snackbar if sharing fails
                        snackbarHostState.showSnackbar(
                            message = "Unable to share movie details",
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                // ==================== USER FEEDBACK EFFECTS ====================

                is MovieDetailsEffect.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = if (effect.isError) SnackbarDuration.Long else SnackbarDuration.Short
                    )

                    // Handle snackbar actions if needed
                    when (result) {
                        SnackbarResult.ActionPerformed -> {
                            // User clicked snackbar action
                            // Could be used for retry actions
                        }

                        SnackbarResult.Dismissed -> {
                            // User dismissed snackbar
                            // Clean up if needed
                        }
                    }
                }

                // ==================== NETWORK-AWARE EFFECTS ====================

                is MovieDetailsEffect.ShowNetworkSnackbar -> {
                    val message = if (effect.isOffline) {
                        "📱 ${effect.message} - Showing cached content"
                    } else {
                        "📶 ${effect.message}"
                    }

                    val result = snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = if (effect.isOffline) "Retry" else null,
                        duration = SnackbarDuration.Long
                    )

                    // Handle retry action for network snackbars
                    if (result == SnackbarResult.ActionPerformed && effect.isOffline) {
                        // EVENT: User tapped retry on network snackbar
                        viewModel.onEvent(MovieDetailsEvent.RefreshDetails)
                    }

                    // EVENT: Dismiss network error from state after showing snackbar
                    viewModel.onEvent(MovieDetailsEvent.DismissNetworkSnackbar)
                }
            }
        }
    }

    // Render the screen
    MovieDetailsScreen(
        contract = viewModel,
        snackbarHostState = snackbarHostState
    )
}

/**
 * TEACHING MOMENT: Enhanced Route Pattern with Event System
 *
 * KEY IMPROVEMENTS:
 *
 * 1. EVENT-DRIVEN FEEDBACK LOOP:
 *    - Effects trigger → Route handles → Events sent back to ViewModel
 *    - Example: ShowNetworkSnackbar → User taps retry → RefreshDetails event
 *    - Maintains unidirectional data flow
 *
 * 2. NETWORK-AWARE USER INTERACTIONS:
 *    - Network snackbars have retry actions
 *    - Automatic dismissal after user interaction
 *    - Contextual messaging based on offline/online state
 *
 * 3. SEPARATION OF CONCERNS:
 *    Route Responsibilities:
 *    ✅ Side effects (navigation, system sharing, snackbars)
 *    ✅ System integrations (Android Intent system)
 *    ✅ Platform-specific operations
 *    ✅ Effect → Event conversion (retry actions)
 *
 *    Screen Responsibilities:
 *    ✅ Pure UI rendering
 *    ✅ State-driven UI logic
 *    ✅ User interaction → Event conversion
 *    ✅ Layout and styling
 *
 * 4. ERROR RESILIENCE:
 *    - Graceful handling of system failures (sharing)
 *    - Fallback mechanisms for platform operations
 *    - User feedback for all operations
 *
 * 5. TESTABILITY ENHANCEMENTS:
 *    - Screen can be tested with fake contracts
 *    - Route testing focuses on effect handling
 *    - Platform dependencies are isolated
 *    - Event flow is traceable and testable
 *
 * EFFECT HANDLING PATTERNS:
 *
 * NavigateBack:
 * Effect → navController.popBackStack()
 *
 * ShareContent:
 * Effect → Android Intent system → Success/Error feedback
 *
 * ShowSnackbar:
 * Effect → SnackbarHost → User interaction handling
 *
 * ShowNetworkSnackbar:
 * Effect → SnackbarHost with retry action → RefreshDetails event → DismissNetworkSnackbar event
 *
 * BENEFITS:
 *
 * 1. CONSISTENT USER EXPERIENCE:
 *    - All network issues provide retry mechanisms
 *    - Contextual feedback for all operations
 *    - Graceful degradation of functionality
 *
 * 2. MAINTAINABLE ARCHITECTURE:
 *    - Clear effect handling patterns
 *    - Reusable across different screens
 *    - Easy to add new effects
 *    - Platform changes isolated to route layer
 *
 * 3. ROBUST ERROR HANDLING:
 *    - Multiple fallback mechanisms
 *    - User-friendly error messages
 *    - Recovery actions always available
 *    - System failures don't crash the app
 *
 * 4. FUTURE-PROOF DESIGN:
 *    - Easy to add new effects (deep links, analytics, etc.)
 *    - Platform-agnostic event system
 *    - Scalable to complex user flows
 *    - Supports A/B testing and feature flags
 *
 * USAGE FLOW EXAMPLE:
 *
 * 1. User loses internet connection
 * 2. ViewModel detects network issue
 * 3. ViewModel emits ShowNetworkSnackbar effect
 * 4. Route shows snackbar with "Retry" action
 * 5. User taps "Retry"
 * 6. Route sends RefreshDetails event to ViewModel
 * 7. ViewModel attempts refresh
 * 8. Route sends DismissNetworkSnackbar event
 * 9. ViewModel updates state to hide snackbar
 * 10. Cycle continues based on network result
 *
 * TESTING STRATEGY:
 *
 * Route Testing:
 * - Mock NavController and verify navigation calls
 * - Mock Context and verify Intent creation
 * - Test effect → event conversion
 * - Verify snackbar interactions
 *
 * Screen Testing:
 * - Test with different UiStates
 * - Verify event emission on user interactions
 * - Test UI rendering in isolation
 * - Verify accessibility
 *
 * Integration Testing:
 * - Test complete user flows
 * - Verify network-aware behavior
 * - Test error recovery scenarios
 * - Verify data preservation patterns
 */