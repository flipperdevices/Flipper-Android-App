package com.flipperdevices.core.ui.res

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class Pallet(
    val accent: Color,
    val accentSecondary: Color,
    val background: Color,
    val text_100: Color,
    val text_88: Color,
    val text_80: Color,
    val text_60: Color,
    val text_40: Color,
    val text_30: Color,
    val text_20: Color,
    val text_16: Color,
    val text_12: Color,
    val text_8: Color,
    val text_4: Color,
    val surface: Color
) {
    @Composable
    fun toMaterialColors() = Colors(
        primary = Color.Black,
        primaryVariant = Color.Black,
        secondary = Color.Black,
        secondaryVariant = accent,
        background = Color.Black,
        surface = surface,
        error = Color.Black,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = text_40,
        onError = Color.Black,
        isLight = !isSystemInDarkTheme()
    )
}
