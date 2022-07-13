package com.flipperdevices.core.ui.theme.models

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
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
    val actionOnFlipperText: Color = Color(0xFFFFFFFF),
    val actionOnFlipperIcon: Color = Color(0xFFFFFFFF),

    val placeholder: Color = Color(0xFFDFDFDF),
    val notificationCard: Color = Color(0xFFE9E9E9),
    val forgetFlipper: Color = Color(0xFFF63F3F),
    val progressBarGray: Color = Color(0xFFAAAAAA),
    val onAppBar: Color = Color(0xFF000000),
    val disableBackgroundSwitch: Color = Color(0xFF616161),

    val divider12: Color,
    val disableSwitch: Color,
    val backgroundDialog: Color
) {

    private val animationSpec: AnimationSpec<Color> = tween(durationMillis = 1500)

    @Composable
    private fun animateColor(
        targetValue: Color
    ) = animateColorAsState(targetValue = targetValue, animationSpec = animationSpec).value

    @Composable
    fun switch() = copy(
        accent = animateColor(accent),
        accentSecond = animateColor(accentSecond),
        onError = animateColor(onError),
        background = animateColor(background),
        error = animateColor(error),
        content = animateColor(content),
        onContent = animateColor(onContent),
        text100 = animateColor(text100),
        text88 = animateColor(text88),
        text80 = animateColor(text80),
        text60 = animateColor(text60),
        text40 = animateColor(text40),
        text30 = animateColor(text30),
        text20 = animateColor(text20),
        text16 = animateColor(text16),
        text12 = animateColor(text12),
        text8 = animateColor(text8),
        text4 = animateColor(text4),
        iconTint100 = animateColor(iconTint100),
        iconTint80 = animateColor(iconTint80),
        iconTint30 = animateColor(iconTint30),
        iconTint16 = animateColor(iconTint16),
        bottomBarBackground = animateColor(bottomBarBackground),
        bottomBarSelected = animateColor(bottomBarSelected),
        bottomBarSelectedFlipperStatus = animateColor(bottomBarSelectedFlipperStatus),
        bottomBarUnselected = animateColor(bottomBarTabBackground),
        bottomBarTabBackground = animateColor(bottomBarTabBackground),
        bottomBarUnsupported = animateColor(bottomBarUnsupported),
        bottomBarContent = animateColor(bottomBarContent),
        channelFirmwareReleaseCandidate = animateColor(channelFirmwareReleaseCandidate),
        channelFirmwareRelease = animateColor(channelFirmwareRelease),
        channelFirmwareDev = animateColor(channelFirmwareDev),
        channelFirmwareUnknown = animateColor(channelFirmwareUnknown),
        updateProgressGreen = animateColor(updateProgressGreen),
        updateProgressBackgroundGreen = animateColor(updateProgressBackgroundGreen),
        updateProgressBlue = animateColor(updateProgressBlue),
        updateProgressBackgroundBlue = animateColor(updateProgressBackgroundBlue),
        onFlipperButton = animateColor(onFlipperButton),
        onFirmwareUpdateButton = animateColor(onFirmwareUpdateButton),
        onFirmwareUpdateProgress = animateColor(onFirmwareUpdateProgress),
        keyIButton = animateColor(keyIButton),
        keyRFID = animateColor(keyRFID),
        keyNFC = animateColor(keyNFC),
        keySubGHz = animateColor(keySubGHz),
        keyInfrarred = animateColor(keyInfrarred),
        keyBadUSB = animateColor(keyBadUSB),
        keyUnknown = animateColor(keyUnknown),
        keyDeleted = animateColor(keyDeleted),
        keyFavorite = animateColor(keyFavorite),
        keyTitle = animateColor(keyTitle),
        keyIcon = animateColor(keyIcon),
        keyDelete = animateColor(keyDelete),
        batteryRed = animateColor(batteryRed),
        batteryYellow = animateColor(batteryYellow),
        batteryGreen = animateColor(batteryGreen),
        batteryBackground = animateColor(batteryBackground),
        batteryCharging = animateColor(batteryCharging),
        actionOnFlipperDisable = animateColor(actionOnFlipperDisable),
        actionOnFlipperEnable = animateColor(actionOnFlipperEnable),
        actionOnFlipperText = animateColor(actionOnFlipperText),
        actionOnFlipperIcon = animateColor(actionOnFlipperIcon),
        placeholder = animateColor(placeholder),
        notificationCard = animateColor(notificationCard),
        forgetFlipper = animateColor(forgetFlipper),
        progressBarGray = animateColor(progressBarGray),
        onAppBar = animateColor(onAppBar),
        disableBackgroundSwitch = animateColor(disableBackgroundSwitch),
        divider12 = animateColor(divider12),
        disableSwitch = animateColor(disableSwitch),
        backgroundDialog = animateColor(backgroundDialog)
    )

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
