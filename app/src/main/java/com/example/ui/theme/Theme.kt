package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkDotlineColorScheme = darkColorScheme(
    primary = DotlineTextPrimaryDark,
    onPrimary = DotlineBlack,
    primaryContainer = DotlineCardRaisedDark,
    onPrimaryContainer = DotlineTextPrimaryDark,
    secondary = DotlineTextSecondaryDark,
    onSecondary = DotlineBlack,
    secondaryContainer = DotlineCardDark,
    onSecondaryContainer = DotlineTextPrimaryDark,
    tertiary = DotlineRed,
    onTertiary = Color.White,
    background = DotlineBlack,
    onBackground = DotlineTextPrimaryDark,
    surface = DotlineCardDark,
    onSurface = DotlineTextPrimaryDark,
    surfaceVariant = DotlineCardRaisedDark,
    onSurfaceVariant = DotlineTextSecondaryDark,
    outline = DotlineOutlineDark
)

val LightDotlineColorScheme = lightColorScheme(
    primary = DotlineTextPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = DotlineCardRaisedLight,
    onPrimaryContainer = DotlineTextPrimaryLight,
    secondary = DotlineTextSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = DotlineCardLight,
    onSecondaryContainer = DotlineTextPrimaryLight,
    tertiary = DotlineRed,
    onTertiary = Color.White,
    background = DotlineLightBg,
    onBackground = DotlineTextPrimaryLight,
    surface = DotlineCardLight,
    onSurface = DotlineTextPrimaryLight,
    surfaceVariant = DotlineCardRaisedLight,
    onSurfaceVariant = DotlineTextSecondaryLight,
    outline = DotlineOutlineLight
)

@Composable
fun DotlineTheme(
    themeMode: String = "dark", // "dark", "light", "system"
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkDotlineColorScheme else LightDotlineColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DotlineTypography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    DotlineTheme(
        themeMode = if (darkTheme) "dark" else "light",
        content = content
    )
}
