package com.memoryengine.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = DeepGreen,
    onPrimary = PaperBase,
    primaryContainer = DeepGreenLight,
    onPrimaryContainer = DeepGreen,
    secondary = EditorialMuted,
    onSecondary = PaperBase,
    background = PaperBase,
    onBackground = CharcoalText,
    surface = PaperSurface,
    onSurface = CharcoalText,
    surfaceVariant = PaperSurfaceVariant,
    onSurfaceVariant = EditorialMuted,
    outline = EditorialBorder,
    outlineVariant = EditorialBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen,
    onPrimary = DarkPaperBase,
    primaryContainer = DarkGreenContainer,
    onPrimaryContainer = DarkOnGreenContainer,
    secondary = DarkEditorialMuted,
    onSecondary = DarkPaperBase,
    background = DarkPaperBase,
    onBackground = DarkCharcoalText,
    surface = DarkPaperSurface,
    onSurface = DarkCharcoalText,
    surfaceVariant = DarkPaperSurfaceVariant,
    onSurfaceVariant = DarkEditorialMuted,
    outline = DarkEditorialBorder,
    outlineVariant = DarkEditorialBorder
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun MemoryEngineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
