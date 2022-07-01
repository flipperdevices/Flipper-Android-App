package com.flipperdevices.core.ui.theme

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

    val iconTint100: Color,
    val iconTint80: Color,
    val iconTint30: Color,
    val iconTint16: Color,

    val deletedCategoryType: Color,
    val progressBar: Color,

    val favorite: Color = Color(0xFFFECF5D),

    val tabInActive: Color = Color(0xFF949494),
    val tabActive: Color = Color(0xFF006EDB),

    val accent: Color = Color(0xFFFF8200),
    val accentSecond: Color = Color(0xFF589DFF),

    val greenFirmware: Color = Color(0xFF2ED832),
    val purpleFirmware: Color = Color(0xFF8A2BE2),
    val redFirmware: Color = Color(0xFFF63F3F),
    val grayFirmware: Color = Color(0xFF919191),

    val greenUpdate: Color = Color(0xFF2ED832),
    val greenUpdateBackground: Color = Color(0xFFA3E899),
    val blueUpdateBackground: Color = Color(0xFFACC9FA),

    val keyIButton: Color = Color(0xFFE1BBA6),
    val keyRFID: Color = Color(0xFFFFF493),
    val keyNFC: Color = Color(0xFF98CEFF),
    val keySubGHz: Color = Color(0xFFA5F4BF),
    val keyInfrarred: Color = Color(0xFFFF928B),
    val keyBadUSB: Color = Color(0xFFFFBEE9),
    val keyUnknown: Color = Color(0xFF999999),
    val keyDeleted: Color,

    val selectedBottomBar: Color,
    val unselectedBottomBar40: Color,
    val unselectedBottomBar30: Color,
    val backgroundBottomBar: Color,
    val redContentBottomBar: Color = Color(0xFFF63F3F),

    val unsynchronized: Color,
    val divider12: Color,
    val notificationCard: Color,

    val batteryBackground: Color,
    val batteryRed: Color = Color(0xFFF63F3F),
    val batteryYellow: Color = Color(0xFFFECF5D),
    val batteryGreen: Color = Color(0xFF34C7A4),

    val redForgot: Color = Color(0xFFF63F3F),
    val onButton100: Color,
    val placeholder: Color,
    val switchUncheckedTrack: Color,

    val surface: Color,
    val onSurface: Color
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
        onSurface = onSurface,
        onError = Color.Black,
        isLight = isLight
    )
}
