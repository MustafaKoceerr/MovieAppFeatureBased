package com.mustafakocer.core_ui.ui.theme

import androidx.compose.ui.graphics.Color

// This file defines the specific color palettes for both the light and dark themes,
// adhering to the Material 3 color system.

// --- Brand & Accent Colors ---
val BrandGold = Color(0xFFFFC107) // Gold: Used for primary actions, ratings, and highlights.
val BrandBlue = Color(0xFF0D47A1) // Deep Blue: Used for links and secondary actions.

// --- Dark Theme Palette (Cinematic Night) ---
val DarkPrimary = Color(0xFF1A2C42)
val DarkOnPrimary = Color(0xFFFFFFFF)
val DarkPrimaryContainer = Color(0xFF2C3E50)
val DarkOnPrimaryContainer = Color(0xFFE0E0E0)

val DarkSecondary = BrandGold
val DarkOnSecondary = Color(0xFF000000)
val DarkSecondaryContainer = Color(0x33FFC107) // Gold with ~20% opacity for subtle highlights.
val DarkOnSecondaryContainer = BrandGold

val DarkTertiary = Color(0xFF82B1FF) // A brighter, more vibrant version of BrandBlue for accents.
val DarkOnTertiary = Color(0xFF002E69)
val DarkTertiaryContainer = BrandBlue
val DarkOnTertiaryContainer = Color(0xFFD6E3FF)

val DarkBackground = Color(0xFF0F172A)      // A very dark, slightly blue-tinted background.
val DarkOnBackground = Color(0xFFE2E8F0)
val DarkSurface = Color(0xFF1E293B)         // The color for elevated surfaces like Cards.
val DarkOnSurface = Color(0xFFCBD5E1)
val DarkSurfaceVariant = Color(0xFF334155)  // A slightly lighter surface for differentiation.
val DarkOnSurfaceVariant = Color(0xFF94A3B8)

val DarkError = Color(0xFFEF4444)           // A vibrant red for high-visibility error states.
val DarkOnError = Color(0xFFFFFFFF)

// --- Light Theme Palette (Clean Studio Light) ---
val LightPrimary = Color(0xFF2C3E50)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFD6EAF8)
val LightOnPrimaryContainer = Color(0xFF1A2C42)

val LightSecondary = BrandGold
val LightOnSecondary = Color(0xFF000000)
val LightSecondaryContainer = Color(0x33FFC107)
val LightOnSecondaryContainer = Color(0xFFB8860B) // A darker gold for better contrast on light containers.

val LightTertiary = BrandBlue
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFD6E3FF)
val LightOnTertiaryContainer = Color(0xFF001B3E)

val LightBackground = Color(0xFFF8FAFC)     // An almost-white, clean background.
val LightOnBackground = Color(0xFF0F172A)
val LightSurface = Color(0xFFFFFFFF)        // The color for elevated surfaces like Cards.
val LightOnSurface = Color(0xFF1E293B)
val LightSurfaceVariant = Color(0xFFE2E8F0)
val LightOnSurfaceVariant = Color(0xFF475569)

val LightError = Color(0xFFB91C1C)           // A darker, more accessible red for light backgrounds.
val LightOnError = Color(0xFFFFFFFF)