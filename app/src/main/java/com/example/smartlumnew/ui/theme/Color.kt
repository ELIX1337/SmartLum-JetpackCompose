package com.example.smartlumnew.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val SlYellow = Color(0xFFFFCF40)
val WhiteTransparent = Color(0xE6FFFFFF)
val BlackTransparent = Color(0xE6000000)
val GrayTransparent = Color(0x99CCCCCC)

@Composable
fun themeTransparent(reverse: Boolean = false): Color {
    return if (isAppInDarkTheme() != reverse) BlackTransparent else WhiteTransparent
}

@Composable
fun contrastTransparent(): Color {
    return GrayTransparent
}

fun Color.withAlpha(alpha: Float): Color {
    return Color(red, green, blue, alpha)
}
