package com.example.pomodoro.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

// Deep Work Theme Colors - Dark and Focused
val DeepWorkColors = Colors(
    primary = Color(0xFF7C4DFF),       // Soft purple accent
    primaryVariant = Color(0xFF5C35CC),
    secondary = Color(0xFF03DAC6),      // Teal for break mode
    secondaryVariant = Color(0xFF018786),
    background = Color(0xFF0D0D0D),     // Deep black
    surface = Color(0xFF1A1A1A),        // Slightly lighter surface
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

// Status Colors
object StatusColors {
    val Focus = Color(0xFF7C4DFF)        // Purple for focus
    val ShortBreak = Color(0xFF03DAC6)   // Teal for short break
    val LongBreak = Color(0xFF4CAF50)    // Green for long break
    val DeepWork = Color(0xFFFF7043)     // Orange for deep work
    val Paused = Color(0xFF9E9E9E)       // Gray for paused
}

@Composable
fun PomodoroTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DeepWorkColors,
        content = content
    )
}
