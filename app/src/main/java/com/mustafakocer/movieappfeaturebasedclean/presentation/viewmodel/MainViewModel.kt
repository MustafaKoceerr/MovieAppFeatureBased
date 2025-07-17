package com.mustafakocer.movieappfeaturebasedclean.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.core_preferences.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val languageRepository: LanguageRepository,
) : ViewModel() {

    // Future: User session, app state, etc. buraya eklenebilir
    // val userFlow: Flow<User> = userRepository.userFlow
    // val appStateFlow: Flow<AppState> = appStateRepository.stateFlow
    // Flow'u, UI için optimize edilmiş bir StateFlow'a dönüştür.
    val themeState: StateFlow<ThemePreference> = themeRepository.themeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemePreference.SYSTEM // Bu sadece geçici bir fallback değeri.
    )

    val languageState: StateFlow<LanguagePreference> = languageRepository.languageFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LanguagePreference.DEFAULT
    )
}