package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = ForestGreenOnPrimary,
    primaryContainer = ForestGreenContainer,
    onPrimaryContainer = ForestGreenOnContainer,
    secondary = ParijotoBerrySecondary,
    onSecondary = ParijotoBerryOnSecondary,
    secondaryContainer = ParijotoBerryContainer,
    onSecondaryContainer = ParijotoBerryOnContainer,
    tertiary = MuriaPurpleTertiary,
    onTertiary = MuriaPurpleOnTertiary,
    tertiaryContainer = MuriaPurpleContainer,
    onTertiaryContainer = MuriaPurpleOnContainer,
    background = SoftBackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorRed,
    errorContainer = ErrorContainerRed
)

@Composable
fun ParijotoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // Fokus pada tema terang alaminya
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            if (context is Activity) {
                val window = context.window
                @Suppress("DEPRECATION")
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ParijotoTypography,
        content = content
    )
}

@Composable
fun ELKPDTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    ParijotoTheme(darkTheme = darkTheme, content = content)
}
