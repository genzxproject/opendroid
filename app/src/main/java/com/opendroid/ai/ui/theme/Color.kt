package com.opendroid.ai.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * OpenDroid color palette — Claymorphism "soft surface" theme.
 * Soft clay tones: warm cream backgrounds, muted clay accents, soft shadows.
 */
data class OpenDroidColors(
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    val borderColor: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accentNeonGreen: Color,
    /** Calmer green for large filled button surfaces (neon is for thin marks only). */
    val accentGreenButton: Color,
    val accentPurple: Color,
    val accentCyan: Color,
    val accentRed: Color,
    val isDark: Boolean
)

// ── Dark palette — soft clay (warm dark) ────────────────────
val DarkPalette = OpenDroidColors(
    background = Color(0xFF1C1917),      // warm near-black
    surface = Color(0xFF1C1917),          // seamless with bg (clay: surface == bg)
    cardBackground = Color(0xFF262320),   // raised soft surface
    borderColor = Color(0xFF3D3833),      // soft warm border
    textPrimary = Color(0xFFEDE7E0),
    textSecondary = Color(0xFFA8A29E),
    accentNeonGreen = Color(0xFFA3B18A),  // muted sage
    accentGreenButton = Color(0xFF7D8F69),
    accentPurple = Color(0xFF9C8ABF),     // soft lavender
    accentCyan = Color(0xFF7FB5B5),       // dusty teal
    accentRed = Color(0xFFE07A5F),        // terracotta
    isDark = true
)

// ── Light palette — soft clay (warm cream) ──────────────────
val LightPalette = OpenDroidColors(
    background = Color(0xFFF2EDE7),       // warm cream
    surface = Color(0xFFF2EDE7),          // seamless
    cardBackground = Color(0xFFF9F6F2),   // soft raised
    borderColor = Color(0xFFE4DDD4),
    textPrimary = Color(0xFF3B3530),
    textSecondary = Color(0xFF8A837C),
    accentNeonGreen = Color(0xFF7D8F69),  // sage
    accentGreenButton = Color(0xFF6B7D58),
    accentPurple = Color(0xFF8B7FA8),
    accentCyan = Color(0xFF5F8F8F),
    accentRed = Color(0xFFC96F4A),        // terracotta
    isDark = false
)

val LocalOpenDroidColors = compositionLocalOf { DarkPalette }

/** Access the active palette from any @Composable */
object AppTheme {
    val colors: OpenDroidColors
        @Composable
        @ReadOnlyComposable
        get() = LocalOpenDroidColors.current
}

// ── Legacy top-level aliases ────────────────────────────────
val DarkBackground = DarkPalette.background
val DarkSurface = DarkPalette.surface
val CardBackground = DarkPalette.cardBackground
val BorderColor = DarkPalette.borderColor
val TextPrimary = DarkPalette.textPrimary
val TextSecondary = DarkPalette.textSecondary
val AccentNeonGreen = DarkPalette.accentNeonGreen
val AccentGreenButton = DarkPalette.accentGreenButton
val AccentPurple = DarkPalette.accentPurple
val AccentCyan = DarkPalette.accentCyan
val AccentRed = DarkPalette.accentRed
