package com.flipperdevices.core.ui.theme.models

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Colors
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

// After add color to pallet, don`t forget add to switch() method
@Stable
@Suppress("MagicNumber")
data class FlipperPallet(
    val accent: Color = Color(0xFFFF8200),
    val accentSecond: Color = Color(0xFF589DFF),
    val onError: Color = Color(0xFFF63F3F),
    val background: Color,
    val error: Color,
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

    val bottomBarBackground: Color = Color(0xFFFFFFFF),
    val bottomBarSelected: Color = Color(0xFF303030),
    val bottomBarSelectedFlipperStatus: Color = Color(0xFF919191),
    val bottomBarUnselected: Color = Color(0xFFAAAAAA),
    val bottomBarTabBackground: Color = Color(0xFFE9E9E9),
    val bottomBarUnsupported: Color = Color(0xFFF63F3F),
    val bottomBarContent: Color = Color(0xFF000000),

    val channelFirmwareReleaseCandidate: Color,
    val channelFirmwareRelease: Color = Color(0xFF2ED832),
    val channelFirmwareDev: Color = Color(0xFFF63F3F),
    val channelFirmwareUnknown: Color = Color(0xFF919191),

    val updateProgressGreen: Color = Color(0xFF2ED832),
    val updateProgressBackgroundGreen: Color = Color(0xFFA3E899),
    val updateProgressBlue: Color = Color(0xFF589DFF),
    val updateProgressBackgroundBlue: Color = Color(0xFFACC9FA),

    val onFlipperButton: Color = Color(0xFFFFFFFF),
    val onFirmwareUpdateButton: Color = Color(0xFFFFFFFF),
    val onFirmwareUpdateProgress: Color = Color(0xFFFFFFFF),

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

    val actionOnFlipperDisable: Color = Color(0xFF919191),
    val actionOnFlipperEnable: Color = Color(0xFF589DFF),
    val actionOnFlipperProgress: Color = Color(0xFF89B9FE),
    val actionOnFlipperSubGhzEnable: Color = Color(0xFFFF8200),
    val actionOnFlipperSubGhzProgress: Color = Color(0xFFFEA64B),
    val actionOnFlipperText: Color = Color(0xFFFFFFFF),
    val actionOnFlipperIcon: Color = Color(0xFFFFFFFF),

    val placeholder: Color = Color(0xFFDFDFDF),
    val notificationCard: Color,
    val forgetFlipper: Color = Color(0xFFF63F3F),
    val progressBarGray: Color = Color(0xFFAAAAAA),
    val onAppBar: Color = Color(0xFF000000),
    val disableBackgroundSwitch: Color = Color(0xFF616161),

    val nfcCardUIDColor: Color = Color(0xFF8A2BE2),
    val nfcCardKeyAColor: Color = Color(0xFF2ED832),
    val nfcCardAccessBitsColor: Color = Color(0xFFF63F3F),
    val nfcCardKeyBColor: Color = Color(0xFF589DFF),

    val divider12: Color,
    val disableSwitch: Color,
    val backgroundDialog: Color,

    val nfcCardBackground: Color = Color(0xFF4A4A4A),
    val nfcCardBackgroundSecond: Color = Color(0xFF303030),
    val onNfcCard: Color = Color(0xFFFFFFFF),

    val warningColor: Color = Color(0xFFF63F3F),
    val successfullyColor: Color = Color(0xFF2ED832),
    val textOnWarningBackground: Color = Color(0xFFFFFFFF),

    val substrateActiveCellNfcEditor: Color,
    val hexKeyboardBackground: Color,

    val bubbleEmulateBackground: Color = Color(0xFFC1C1C1),
    val bubbleEmulate: Color = Color(0xFFFFFFFF),

    val textSelectionHandle: Color = accent,
    val textSelectionBackground: Color = accent.copy(alpha = 0.3f),

    val calculationMfKey32: Color = accent,
    val calculationMfKey32Background: Color = accent.copy(alpha = 0.54f),

    val shareSheetBackground: Color,
    val shareSheetBackgroundAction: Color = Color(0xFFFF8200),
    val shareSheetScrimColor: Color,
    val fapHubSwitchBackground: Color,
    val fapHubSelectedBackgroundColor: Color,
    val fapHubActiveColor: Color = text100,
    val fapHubInactiveColor: Color = text40,
    val fapHubIndicationColor: Color = Color(0xFFFFFFFF),
    val fapScreenshotBorder: Color = Color(0xFF000000),
    val fapHubDividerColor: Color,
    val fapHubSortedColor: Color,
    val fapHubCategoryText: Color
) {
    internal fun toMaterialColors(isLight: Boolean) = Colors(
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

    internal fun toTextSelectionColors() = TextSelectionColors(
        handleColor = textSelectionHandle,
        backgroundColor = textSelectionBackground
    )
}
