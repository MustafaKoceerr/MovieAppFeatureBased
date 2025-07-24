package com.mustafakocer.movieappfeaturebasedclean.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.core_preferences.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * A top-level ViewModel for the main application container (e.g., `MainActivity`).
 *
 * Architectural Decision: This ViewModel's primary responsibility is to hold and expose global,
 * application-wide UI state that affects the entire app, such as the current theme and language.
 * By providing this state from a central, lifecycle-aware location, the root UI layer can
 * reactively update the app's appearance and configuration without needing to manage this state itself.
 * This is a clean way to handle cross-cutting concerns that don't belong to any single feature.
 *
 * @param themeRepository The repository for managing theme preferences.
 * @param languageRepository The repository for managing language preferences.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val languageRepository: LanguageRepository,
) : ViewModel() {

    /**
     * A hot [StateFlow] that emits the current application theme preference.
     * The UI layer (e.g., the root composable) collects this state to dynamically set the app's theme.
     */
    val themeState: StateFlow<ThemePreference> = themeRepository.themeFlow
        // Architectural Decision: The `stateIn` operator converts the "cold" Flow from the repository
        // into a "hot" StateFlow, which is optimized for UI state consumption.
        .stateIn(
            scope = viewModelScope,
            // `SharingStarted.WhileSubscribed(5000)` is a crucial performance optimization. It keeps
            // the upstream flow active for 5 seconds after the last UI collector unsubscribes. This
            // prevents the flow from being restarted during brief interruptions like screen rotations,
            // providing a seamless user experience.
            started = SharingStarted.WhileSubscribed(5000),
            // An initial value is provided as a temporary fallback before the first value from the
            // repository's flow is emitted.
            initialValue = ThemePreference.SYSTEM
        )

}