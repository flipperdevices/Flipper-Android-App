package com.flipperdevices.core.ui.theme.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.core.preference.pb.SelectedTheme
import com.flipperdevices.core.ui.theme.models.FlipperPallet
import com.flipperdevices.core.ui.theme.viewmodel.ThemeViewModel

/**
 * Please, use LocalPallet instead
 *
 * @return the necessary Pallet depending on the theme
 */
@Composable
fun getThemedFlipperPallet(systemIsDark: Boolean): FlipperPallet {
    val isLight = isLight(systemIsDark)
    return if (isLight) lightPallet else darkPallet
}

@Composable
fun isLight(systemIsDark: Boolean): Boolean {
    val themeViewModel: ThemeViewModel = viewModel()
    val theme by themeViewModel.getAppTheme().collectAsState()

    return when (theme) {
        SelectedTheme.LIGHT -> true
        SelectedTheme.DARK -> false
        else -> !systemIsDark
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
    bottomBarBackground = Color(0xFFFFFFFF),
    disableSwitch = Color(0xFFDFDFDF),
    backgroundDialog = Color(0xFFFFFFFF),
    notificationCard = Color(0xFFE9E9E9),

    substrateActiveCellNfcEditor = Color(0xFFDFDFDF),
    hexKeyboardBackground = Color(0xFF616161)
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
    bottomBarBackground = Color(0xFF1C1C1C),
    disableSwitch = Color(0xFF616161),
    backgroundDialog = Color(0xFF303030),
    notificationCard = Color(0xFF616161),

    substrateActiveCellNfcEditor = Color(0xFF616161),
    hexKeyboardBackground = Color(0xFF000000)
)
