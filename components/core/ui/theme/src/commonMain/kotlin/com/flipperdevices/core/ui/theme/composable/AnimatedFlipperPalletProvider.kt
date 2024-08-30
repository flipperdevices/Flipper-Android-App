package com.flipperdevices.core.ui.theme.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private const val ANIMATION_DURATION_MS = 750
private val animationSpec: AnimationSpec<Color> = tween(ANIMATION_DURATION_MS)

@Composable
private fun animateColor(
    targetValue: Color
) = animateColorAsState(targetValue = targetValue, animationSpec = animationSpec).value

@Composable
internal fun FlipperPallet.toAnimatePallet() = FlipperPallet(
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

    divider12 = animateColor(divider12),
    channelFirmwareReleaseCandidate = animateColor(channelFirmwareReleaseCandidate),
    bottomBarSelected = animateColor(bottomBarSelected),
    bottomBarBackground = animateColor(bottomBarBackground),
    disableSwitch = animateColor(disableSwitch),
    backgroundDialog = animateColor(backgroundDialog),
    notificationCard = animateColor(notificationCard),

    substrateActiveCellNfcEditor = animateColor(substrateActiveCellNfcEditor),
    hexKeyboardBackground = animateColor(hexKeyboardBackground),

    shareSheetBackground = animateColor(shareSheetBackground),
    shareSheetScrimColor = animateColor(shareSheetScrimColor).copy(alpha = 0.15f),
    fapHubSwitchBackground = animateColor(fapHubSwitchBackground),
    fapHubSelectedBackgroundColor = animateColor(fapHubSelectedBackgroundColor),
    fapHubDividerColor = animateColor(fapHubDividerColor),
    fapHubSortedColor = animateColor(fapHubSortedColor),
    fapHubCategoryText = animateColor(fapHubCategoryText),
    fapHubDeleteDialogBackground = animateColor(fapHubDeleteDialogBackground),

    fapHubBuildStatusReadyBackground = animateColor(fapHubBuildStatusReadyBackground),
    fapHubBuildStatusReadyText = animateColor(fapHubBuildStatusReadyText),
    fapHubBuildStatusRebuildingBackground = animateColor(fapHubBuildStatusRebuildingBackground),
    fapHubBuildStatusRebuildingText = animateColor(fapHubBuildStatusRebuildingText),
    fapHubBuildStatusFailedBackground = animateColor(fapHubBuildStatusFailedBackground),
    fapHubBuildStatusFailedText = animateColor(fapHubBuildStatusFailedText),

    flipperDisableButton = animateColor(flipperDisableButton),
    reportBorder = animateColor(reportBorder),

    screenStreamingBorderColor = animateColor(screenStreamingBorderColor),
    screenStreamingNotConnectedColor = animateColor(screenStreamingNotConnectedColor),

    shareSheetNavigationBarActiveColor = animateColor(shareSheetNavigationBarActiveColor),
    shareSheetStatusBarActiveColor = animateColor(shareSheetStatusBarActiveColor),
    shareSheetNavigationBarDefaultColor = animateColor(shareSheetNavigationBarDefaultColor),
    shareSheetStatusBarDefaultColor = animateColor(shareSheetStatusBarDefaultColor),

    keyScreenDisabled = animateColor(keyScreenDisabled),

    placeholder = animateColor(placeholder)
)
