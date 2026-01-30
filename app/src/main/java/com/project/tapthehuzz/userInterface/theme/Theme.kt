package com.project.tapthehuzz.userInterface.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentRed,
    onPrimary = White,
    primaryContainer = AccentRedDark,
    onPrimaryContainer = White,
    secondary = GlassSurfaceLight,
    onSecondary = White,
    secondaryContainer = GlassSurface,
    onSecondaryContainer = White,
    tertiary = Pink80,
    background = TrueBlack,
    onBackground = TextPrimary,
    surface = GlassSurface,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorderLight,
    error = Color(0xFFCF6679),
    errorContainer = Color(0xFFB00020),
    onError = White,
    onErrorContainer = White
)

private val LightColorScheme = lightColorScheme(
    primary = AccentRed,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun TapTheHuzzTheme(
    darkTheme: Boolean = true, // Always use dark theme for glassmorphism
    dynamicColor: Boolean = false, // Disable dynamic colors to maintain glass theme
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme // Always use dark scheme for glass effect

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = TrueBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
