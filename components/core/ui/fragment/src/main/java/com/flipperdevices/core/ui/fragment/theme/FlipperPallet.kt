package com.flipperdevices.core.ui.fragment.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Suppress("MagicNumber")
data class FlipperPallet(
    val background: Color,
    val text100: Color,
    val text88: Color,
    val text80: Color,
    val text60: Color,
    val text40: Color,
    val text30: Color,
    val text20: Color,
    val text16: Color,
    val text12: Color,
    val text8: Color,
    val text4: Color,
    val surface: Color,

    val accent: Color = Color(0xFFFF8200),
    val accentSecond: Color = Color(0xFF589DFF),

    val greenFirmware: Color = Color(0xFF2ED832),
    val purpleFirmware: Color = Color(0xFF8A2BE2),
    val redFirmware: Color = Color(0xFFF63F3F),

    val keyIButton: Color = Color(0xFFE1BBA6),
    val keyRFID: Color = Color(0xFFFFF493),
    val keyNFC: Color = Color(0xFF98CEFF),
    val keySubGHz: Color = Color(0xFFA5F4BF),
    val keyInfrarred: Color = Color(0xFFFF928B),
    val keyBadUSB: Color = Color(0xFFFFBEE9)
) {
    @Composable
    fun toMaterialColors(isLight: Boolean) = Colors(
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
        onSurface = text40,
        onError = Color.Black,
        isLight = isLight
    )
}
