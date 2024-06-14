package com.example.myfreehealthtracker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.myfreehealthtracker.ColorPalette.HonoluluBlue


object ColorPalette {
    val Orange800 = Color(0xFFEF6C00)
    val Blue800 = Color(0xFF0277BD)
    val Teal800 = Color(0xFF00838F)
    val Orange100 = Color(0xFFFFE0B2)
    val HonoluluBlue = Color(0xFF0496FF)
}

private val DarkColorPalette = darkColorScheme(
    primary = HonoluluBlue,
    secondary = Color.White,
    onPrimary = Color.White,
    background = Color(0xFF1B1A1F),
)

private val LightColorPalette = lightColorScheme(
    primary = HonoluluBlue,
    onPrimary = Color(0xFF1B1A1F),
    secondary = Color(0xFF1B1A1F),
    background = Color.Transparent,

    )
private val AppTypography = androidx.compose.material3.Typography(

)
private val AppShapes = androidx.compose.material3.Shapes()

@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}