package com.example.smartshopping.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MintPrimary = Color(0xFF1FC7BE)
private val MintPrimaryDark = Color(0xFF14A8A0)
private val MintSecondary = Color(0xFF73D8D2)
private val Coral = Color(0xFFFF8D6C)
private val Peach = Color(0xFFFFC08C)
private val Sky = Color(0xFF7ACFF5)

private val LightBackground = Color(0xFFF5FBFB)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceVariant = Color(0xFFF0F7F7)
private val TextPrimary = Color(0xFF22343B)
private val TextSecondary = Color(0xFF728088)
private val BorderColor = Color(0xFFE4F0EF)

private val DarkBackground = Color(0xFF0F171A)
private val DarkSurface = Color(0xFF172226)
private val DarkSurfaceVariant = Color(0xFF223137)
private val DarkTextPrimary = Color(0xFFE8F3F2)
private val DarkTextSecondary = Color(0xFFB6C8C8)

private val LightColors = lightColorScheme(
    primary = MintPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2FAF6),
    onPrimaryContainer = TextPrimary,
    secondary = MintSecondary,
    onSecondary = Color.White,
    tertiary = Coral,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = LightSurface,
    onSurface = TextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderColor
)

private val DarkColors = darkColorScheme(
    primary = MintSecondary,
    onPrimary = Color(0xFF0B1F25),
    primaryContainer = Color(0xFF184B50),
    onPrimaryContainer = Color(0xFFD5FBF8),
    secondary = Sky,
    onSecondary = Color(0xFF10232B),
    tertiary = Peach,
    onTertiary = Color(0xFF381C0E),
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF31454A)
)

@Composable
fun SmartShoppingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}