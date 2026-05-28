package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CarbonDarkPrimary,
    onPrimary = CarbonDarkOnPrimary,
    background = CarbonDarkBackground,
    onBackground = CarbonDarkOnBackground,
    surface = CarbonDarkSurface,
    onSurface = CarbonDarkOnSurface,
    secondary = CarbonDarkSecondary,
    onSecondary = Color.Black,
    tertiary = CarbonDarkTertiary,
    onTertiary = Color.White,
    surfaceVariant = SlateGray,
    onSurfaceVariant = CarbonDarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4C8D00), // Active green for light theme
    onPrimary = Color.White,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF18181B),
    surface = Color.White,
    onSurface = Color(0xFF18181B),
    secondary = Color(0xFF008080),
    onSecondary = Color.White,
    tertiary = Color(0xFFD32F2F),
    onTertiary = Color.White,
    surfaceVariant = Color(0xFFF1F1F1),
    onSurfaceVariant = Color(0xFF18181B)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme by default for elite dark carbon fitness styling
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
