package com.project.weathersnap.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Custom dark olive color scheme to match the assignment's design language.
 * Dynamic color is intentionally disabled to ensure a consistent look.
 */
private val WeatherSnapColorScheme = darkColorScheme(
    primary = OliveAccent,
    onPrimary = OnOliveAccent,
    primaryContainer = OliveAccentDark,
    onPrimaryContainer = TextPrimary,

    secondary = OliveAccentDark,
    onSecondary = OnOliveAccent,
    secondaryContainer = CardDarkVariant,
    onSecondaryContainer = TextPrimary,

    tertiary = ChipTextTeal,
    onTertiary = DarkBackground,

    background = DarkBackground,
    onBackground = TextPrimary,

    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,

    outline = CardBorder,
    outlineVariant = CardBorder,

    error = Color(0xFFCF6679),
    errorContainer = Color(0xFF4A2020),
    onError = Color.White,
    onErrorContainer = Color(0xFFCF6679)
)

@Composable
fun WeatherSnapTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = WeatherSnapColorScheme

    // Make status bar transparent with light icons for the dark theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}