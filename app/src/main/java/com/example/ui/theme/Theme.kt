package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CanvasColorScheme = lightColorScheme(
    primary = CanvasTextPrimary,
    onPrimary = Color.White,
    primaryContainer = CanvasLimeAccent,
    onPrimaryContainer = CanvasLimeOnColor,
    secondary = CanvasBlueAccent,
    onSecondary = Color.White,
    secondaryContainer = CanvasLightBorderSubtle,
    onSecondaryContainer = CanvasTextPrimary,
    tertiary = CanvasLimeAccent,
    onTertiary = CanvasLimeOnColor,
    background = CanvasLightBackground,
    onBackground = CanvasTextPrimary,
    surface = CanvasLightSurface,
    onSurface = CanvasTextPrimary,
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = CanvasTextSecondary,
    outline = CanvasLightBorder,
    outlineVariant = CanvasLightBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CanvasColorScheme,
        typography = Typography,
        content = content
    )
}
