package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianBackground,
    primaryContainer = GoldContainerDark,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = ObsidianBackground,
    secondaryContainer = ObsidianSurfaceElevated,
    onSecondaryContainer = CreamText,
    tertiary = CreamSurfaceVariant,
    onTertiary = ObsidianBackground,
    background = ObsidianBackground,
    onBackground = TextLightPrimary,
    surface = ObsidianSurface,
    onSurface = TextLightPrimary,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = TextLightSecondary,
    outline = ObsidianBorder,
    outlineVariant = Color(0xFF262630),
    error = StatusError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = GoldContainerLight,
    onPrimaryContainer = GoldDark,
    secondary = GoldPrimary,
    onSecondary = ObsidianBackground,
    secondaryContainer = CreamSurfaceVariant,
    onSecondaryContainer = TextDarkPrimary,
    tertiary = ObsidianSurface,
    onTertiary = Color.White,
    background = CreamBackground,
    onBackground = TextDarkPrimary,
    surface = CreamSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = CreamSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,
    outline = Color(0xFFDED8CE),
    outlineVariant = Color(0xFFEBE6DC),
    error = StatusError,
    onError = Color.White
)

@Composable
fun FaustiArtsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
