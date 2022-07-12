package com.flipperdevices.core.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Suppress("MagicNumber")
data class FlipperPallet(
    val accent: Color,
    val accentSecond: Color,
    val background: Color,
    val error: Color,
    val onError: Color,
    val content: Color,
    val onContent: Color,

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

    val bottomBarBackground: Color,
    val bottomBarSelected: Color,
    val bottomBarSelectedFlipperStatus: Color,
    val bottomBarUnselected: Color,
    val bottomBarTabBackground: Color,
    val bottomBarUnsupported: Color,
    val bottomBarContent: Color = Color(0xFF000000),

    val channelFirmwareRelease: Color = Color(0xFF2ED832),
    val channelFirmwareReleaseCandidate: Color = Color(0xFF8A2BE2),
    val channelFirmwareDev: Color = Color(0xFFF63F3F),
    val channelFirmwareUnknown: Color = Color(0xFF919191),

    val updateProgressGreen: Color = Color(0xFF2ED832),
    val updateProgressBackgroundGreen: Color = Color(0xFFA3E899),
    val updateProgressBlue: Color = Color(0xFF589DFF),
    val updateProgressBackgroundBlue: Color = Color(0xFFACC9FA),

    val keyIButton: Color = Color(0xFFE1BBA6),
    val keyRFID: Color = Color(0xFFFFF493),
    val keyNFC: Color = Color(0xFF98CEFF),
    val keySubGHz: Color = Color(0xFFA5F4BF),
    val keyInfrarred: Color = Color(0xFFFF928B),
    val keyBadUSB: Color = Color(0xFFFFBEE9),
    val keyUnknown: Color = Color(0xFF999999),
    val keyDeleted: Color = Color(0xFFE9E9E9),
    val keyFavorite: Color = Color(0xFFFECF5D),
    val keyTitle: Color = Color(0xFF000000),
    val keyIcon: Color = Color(0xFF000000),
    val keyDelete: Color = Color(0xFFF63F3F),

    val batteryRed: Color = Color(0xFFF63F3F),
    val batteryYellow: Color = Color(0xFFFECF5D),
    val batteryGreen: Color = Color(0xFF34C7A4),
    val batteryBackground: Color = Color(0xFF8D8E92),
    val batteryCharging: Color = Color(0xFF303030),

    val placeholder: Color = Color(0xFFDFDFDF),
    val divider12: Color = Color(0xFFDFDFDF),
    val notificationCard: Color = Color(0xFFE9E9E9),

    val actionOnFlipperDisable: Color = Color(0xFF919191),
    val actionOnFlipperEnable: Color = Color(0xFF589DFF),
    val actionOnFlipperText: Color = Color(0xFFFFFFFF),
    val actionOnFlipperIcon: Color = Color(0xFFFFFFFF),

    val forgetFlipper: Color = Color(0xFFF63F3F),
    val onFlipperButton: Color = Color(0xFFFFFFFF),
    val onFirmwareUpdateButton: Color = Color(0xFFFFFFFF),
    val onFirmwareUpdateProgress: Color = Color(0xFFFFFFFF),
    val progressBarGray: Color = Color(0xFFAAAAAA),
    val onAppBar: Color = Color(0xFF000000),
    val backgroundSwitch: Color = Color(0xFF616161),
    val disableSwitch: Color,
    val backgroundDialog: Color
) {

    @Composable
    fun toMaterialColors(isLight: Boolean) = Colors(
        primary = content,
        primaryVariant = accentSecond,
        secondary = content,
        secondaryVariant = accent,
        background = background,
        surface = content,
        error = error,
        onPrimary = onContent,
        onSecondary = onContent,
        onBackground = onContent,
        onSurface = onContent,
        onError = onError,
        isLight = isLight
    )
}
