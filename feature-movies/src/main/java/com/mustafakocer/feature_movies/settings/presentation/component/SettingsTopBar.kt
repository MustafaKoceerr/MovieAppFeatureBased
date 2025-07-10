package com.mustafakocer.feature_movies.settings.component

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Ayarlar ekranı için modern TopAppBar.
 * Potansiyel olarak kendi dosyasına taşınabilir: SettingsTopBar.kt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(onBackPressed: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { /* ... (içerik aynı) ... */ },
        navigationIcon = { /* ... (içerik aynı) ... */ },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}