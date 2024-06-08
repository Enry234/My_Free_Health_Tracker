package com.example.myfreehealthtracker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val Orange800 = Color(0xFFEF6C00)
val Blue800 = Color(0xFF0277BD)
val Teal800 = Color(0xFF00838F)
val Orange100 = Color(0xFFFFE0B2)


private val DarkColorPalette = darkColorScheme(
    primary = Blue800,
    onPrimary = Teal800,
    secondary = Orange800,
    background = Color.Black
)

private val LightColorPalette = lightColorScheme(
    primary = Orange800,
    onPrimary = Teal800,
    secondary = Blue800,
    background = Orange100,

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