package com.opendroid.ai.ui.theme

import android.graphics.Color
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

/**
 * Applies edge-to-edge using the app's selected theme rather than the device's system theme.
 *
 * AndroidX uses the system UI mode to select the API 26-28 navigation-bar scrim by default.
 * OpenDroid supports an in-app theme toggle, so use the selected palette instead. On API 29+
 * [SystemBarStyle.auto] keeps both bars transparent, preserving edge-to-edge behavior.
 */
internal fun ComponentActivity.enableOpenDroidEdgeToEdge(isDarkTheme: Boolean) {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.auto(
            lightScrim = Color.TRANSPARENT,
            darkScrim = Color.TRANSPARENT,
            detectDarkMode = { isDarkTheme }
        ),
        navigationBarStyle = SystemBarStyle.auto(
            lightScrim = LightPalette.background.toArgb(),
            darkScrim = DarkPalette.background.toArgb(),
            detectDarkMode = { isDarkTheme }
        )
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = DarkPalette.accentNeonGreen,
    secondary = DarkPalette.accentPurple,
    tertiary = DarkPalette.accentCyan,
    background = DarkPalette.background,
    surface = DarkPalette.surface,
    surfaceVariant = DarkPalette.cardBackground,
    onPrimary = DarkPalette.background,
    onSecondary = DarkPalette.textPrimary,
    onBackground = DarkPalette.textPrimary,
    onSurface = DarkPalette.textPrimary,
    onSurfaceVariant = DarkPalette.textSecondary,
    error = DarkPalette.accentRed
)

private val LightColorScheme = lightColorScheme(
    primary = LightPalette.accentNeonGreen,
    secondary = LightPalette.accentPurple,
    tertiary = LightPalette.accentCyan,
    background = LightPalette.background,
    surface = LightPalette.surface,
    surfaceVariant = LightPalette.cardBackground,
    onPrimary = LightPalette.surface,
    onSecondary = LightPalette.textPrimary,
    onBackground = LightPalette.textPrimary,
    onSurface = LightPalette.textPrimary,
    onSurfaceVariant = LightPalette.textSecondary,
    error = LightPalette.accentRed
)

// Claymorphism: large soft radii everywhere — buttons, cards, inputs
val ClayShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun OpenDroidTheme(
    isDarkTheme: Boolean = true,
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val palette = if (isDarkTheme) DarkPalette else LightPalette
    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (isDarkTheme) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(LocalContext.current)
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    val activity = view.context as? ComponentActivity
    if (!view.isInEditMode && activity != null) {
        SideEffect {
            activity.enableOpenDroidEdgeToEdge(isDarkTheme)
            val window = activity.window
            val insetsController = WindowCompat.getInsetsController(window, view)
            // Light status bar icons for dark theme, dark icons for light theme
            insetsController.isAppearanceLightStatusBars = !isDarkTheme
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme
        }
    }

    CompositionLocalProvider(LocalOpenDroidColors provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = ClayShapes,
            content = content
        )
    }
}
