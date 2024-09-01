package com.flipperdevices.core.ui.theme.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.flipperdevices.core.preference.pb.SelectedTheme

/**
 * Please, use LocalPallet instead
 *
 * @return the necessary Pallet depending on the theme
 */
@Composable
fun getThemedFlipperPallet(isLight: Boolean): FlipperPallet {
    return if (isLight) {
        lightPallet
    } else {
        darkPallet
    }.toAnimatePallet()
}

@Composable
internal fun isLight(
    theme: SelectedTheme,
    systemIsDark: Boolean = isSystemInDarkTheme()
): Boolean {
    return when (theme) {
        SelectedTheme.LIGHT -> true
        SelectedTheme.DARK -> false
        is SelectedTheme.Unrecognized,
        SelectedTheme.SYSTEM -> !systemIsDark
    }
}

@Suppress("MagicNumber")
private val lightPallet = FlipperPallet(
    background = Color(0xFFFBFBFB),
    error = Color(0xFFFFFFFF),
    content = Color(0xFFFFFFFF),
    onContent = Color(0xFF000000),

    text100 = Color(0xFF000000),
    text88 = Color(0xFF1C1C1C),
    text80 = Color(0xFF303030),
    text60 = Color(0xFF616161),
    text40 = Color(0xFF919191),
    text30 = Color(0xFFAAAAAA),
    text20 = Color(0xFFC1C1C1),
    text16 = Color(0xFFCCCCCC),
    text12 = Color(0xFFD6D6D6),
    text8 = Color(0xFFDFDFDF),
    text4 = Color(0xFFE9E9E9),

    iconTint100 = Color(0xFF000000),
    iconTint80 = Color(0xFF303030),
    iconTint30 = Color(0xFFAAAAAA),
    iconTint16 = Color(0xFFCCCCCC),

    divider12 = Color(0xFFDFDFDF),
    channelFirmwareReleaseCandidate = Color(0xFF8A2BE2),
    bottomBarSelected = Color(0xFF000000),
    bottomBarBackground = Color(0xFFFFFFFF),
    disableSwitch = Color(0xFFDFDFDF),
    backgroundDialog = Color(0xFFFFFFFF),
    notificationCard = Color(0xFFE9E9E9),

    substrateActiveCellNfcEditor = Color(0xFFDFDFDF),
    hexKeyboardBackground = Color(0xFF616161),

    shareSheetBackground = Color(0xFFFFFFFF),
    shareSheetScrimColor = Color(0xFF000000).copy(alpha = 0.15f),
    fapHubSwitchBackground = Color(0xFFF1F1F1),
    fapHubSelectedBackgroundColor = Color(0xFFFFFFFF),
    fapHubDividerColor = Color(0xFFE9E9E9),
    fapHubSortedColor = Color(0xFFAAAAAA),
    fapHubCategoryText = Color(0xFF616161),
    fapHubActiveColor = Color(0xFF000000),
    fapHubInactiveColor = Color(0xFF919191),
    fapHubDeleteDialogBackground = Color(0xFFF6F6F6),

    fapHubBuildStatusReadyBackground = Color(0xFFD9FFE5),
    fapHubBuildStatusReadyText = Color(0xFF19672F),
    fapHubBuildStatusRebuildingBackground = Color(0xFFFFF5AA),
    fapHubBuildStatusRebuildingText = Color(0xFF6B5B02),
    fapHubBuildStatusFailedBackground = Color(0xFFFCD1D6),
    fapHubBuildStatusFailedText = Color(0xFFBB2920),

    flipperDisableButton = Color(0xFFC1C1C1),
    reportBorder = Color(0xFFD6D6D6),

    screenStreamingBorderColor = Color(0xFF000000),
    screenStreamingNotConnectedColor = Color(0xFFDFDFDF),

    shareSheetStatusBarActiveColor = Color(0xFF000000).copy(alpha = 0.15f),
    shareSheetStatusBarDefaultColor = Color(0xFFFBFBFB),
    shareSheetNavigationBarActiveColor = Color(0xFFFFFFFF),
    shareSheetNavigationBarDefaultColor = Color(0xFFFBFBFB),

    keyScreenDisabled = Color(0xFFC1C1C1),

    placeholder = Color(0xFFD6D6D6)
)

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
    bottomBarSelected = Color(0xFFE5E5E5),
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
    fapHubDeleteDialogBackground = Color(0xFF232323),

    fapHubBuildStatusReadyBackground = Color(0xFF16251A),
    fapHubBuildStatusReadyText = Color(0xFF197B34),
    fapHubBuildStatusRebuildingBackground = Color(0xFF242000),
    fapHubBuildStatusRebuildingText = Color(0xFFB69903),
    fapHubBuildStatusFailedBackground = Color(0xFF340006),
    fapHubBuildStatusFailedText = Color(0xFFC82419),

    flipperDisableButton = Color(0xFFAAAAAA),
    reportBorder = Color(0xFF616161),

    screenStreamingBorderColor = Color(0x7FFF8200),
    screenStreamingNotConnectedColor = Color(0xFF303030),

    shareSheetStatusBarActiveColor = Color(0xFF000000),
    shareSheetStatusBarDefaultColor = Color(0xFF000000),
    shareSheetNavigationBarActiveColor = Color(0xFF303030),
    shareSheetNavigationBarDefaultColor = Color(0xFF000000),

    keyScreenDisabled = Color(0xFF616161),

    placeholder = Color(0xFF616161)
)
