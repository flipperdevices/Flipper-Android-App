package com.flipperdevices.core.ui.theme.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Please, use LocalPallet instead
 *
 * @return the necessary Pallet depending on the theme
 */

@Composable
fun getThemedFlipperPallet(): FlipperPallet {
    return darkPallet
}

@Composable
fun isLight(): Boolean {
    return false
}

@Suppress("MagicNumber")
private val darkPallet = FlipperPallet(
    background = Color(0xFF000000),
    error = Color(0xFF000000),
    content = Color(0xFF1C1C1C),
    onContent = Color(0xFFFFFFFF),

    text100 = Color(0xFFFFFFFF),
    text88 = Color(0XFFE9E9E9),
    text80 = Color(0XFFDFDFDF),
    text60 = Color(0xFFD6D6D6),
    text40 = Color(0xFFCCCCCC),
    text30 = Color(0xFFC1C1C1),
    text20 = Color(0xFFAAAAAA),
    text16 = Color(0xFF919191),
    text12 = Color(0xFF616161),
    text8 = Color(0xFF616161),
    text4 = Color(0xFF1C1C1C),

    iconTint100 = Color(0xFFFFFFFF),
    iconTint80 = Color(0XFFDFDFDF),
    iconTint30 = Color(0xFFC1C1C1),
    iconTint16 = Color(0xFF919191),

    divider12 = Color(0xFF616161),
    channelFirmwareReleaseCandidate = Color(0xFFAA69FA),
    bottomBarBackground = Color(0xFF1C1C1C),
    disableSwitch = Color(0xFF616161),
    backgroundDialog = Color(0xFF303030),
    notificationCard = Color(0xFF616161),

    substrateActiveCellNfcEditor = Color(0xFF616161),
    hexKeyboardBackground = Color(0xFF000000),

    shareSheetBackground = Color(0xFF303030),
    shareSheetScrimColor = Color(0xFFFFFFFF).copy(alpha = 0.15f),
    fapHubSwitchBackground = Color(0xFF101010),
    fapHubSelectedBackgroundColor = Color(0xFF1C1C1C),
    fapHubDividerColor = Color(0xFF303030),
    fapHubSortedColor = Color(0xFFC1C1C1),
    fapHubCategoryText = Color(0xFFCCCCCC),
    fapHubActiveColor = Color(0xFF000000),
    fapHubInactiveColor = Color(0xFF919191),

    flipperDisableButton = Color(0xFFAAAAAA),
    borderViewReportBug = Color(0xFF616161),

    screenStreamingBorderColor = Color(0x7FFF8200),
)
