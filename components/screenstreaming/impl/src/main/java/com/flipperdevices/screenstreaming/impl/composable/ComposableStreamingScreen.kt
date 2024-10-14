package com.flipperdevices.screenstreaming.impl.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.dialog.composable.FlipperDialog
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.composable.controls.ComposableFlipperControls
import com.flipperdevices.screenstreaming.impl.composable.screen.ComposableFlipperScreenWithOptions
import com.flipperdevices.screenstreaming.impl.model.FlipperLockState
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenshotViewModel
import kotlinx.collections.immutable.toImmutableList
import com.flipperdevices.core.ui.res.R as DesignSystem

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun ComposableStreamingScreen(
    screenStreamingViewModel: ScreenStreamingViewModel,
    screenshotViewModel: ScreenshotViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val flipperScreen by screenStreamingViewModel.getFlipperScreen().collectAsState()
    val buttons by screenStreamingViewModel.getFlipperButtons().collectAsState()
    val lockState by screenStreamingViewModel.getLockState().collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LocalPallet.current.background)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OrangeAppBar(
            titleId = R.string.control_title,
            onBack = onBack
        )
        var showDeprecatedLockDialog by remember { mutableStateOf(false) }
        if (showDeprecatedLockDialog) {
            ComposableDeprecatedLockDialog(onDismiss = { showDeprecatedLockDialog = false })
        }
        ComposableFlipperScreenWithOptions(
            modifier = Modifier.weight(1f),
            flipperScreen = flipperScreen,
            buttons = buttons.toImmutableList(),
            onTakeScreenshot = { screenshotViewModel.shareScreenshot(flipperScreen) },
            lockState = lockState,
            onClickLockButton = {
                lockState.let {
                    when (it) {
                        FlipperLockState.NotInitialized -> {}
                        FlipperLockState.NotSupported -> showDeprecatedLockDialog = true
                        is FlipperLockState.Ready -> screenStreamingViewModel.onChangeLock(!it.isLocked)
                    }
                }
            }
        )
        ComposableFlipperControls(
            onPressButton = {
                screenStreamingViewModel.onPressButton(it, Gui.InputType.SHORT)
            },
            onLongPressButton = {
                screenStreamingViewModel.onPressButton(it, Gui.InputType.LONG)
            }
        )
    }
}

@Composable
private fun ComposableDeprecatedLockDialog(
    onDismiss: () -> Unit
) {
    FlipperDialog(
        buttonText = stringResource(R.string.control_options_dialog_btn),
        onClickButton = onDismiss,
        onDismissRequest = onDismiss,
        title = stringResource(R.string.control_options_dialog_title),
        text = stringResource(R.string.control_options_dialog_desc),
        imageComposable = {
            Image(
                modifier = Modifier.size(size = 96.dp),
                painter = painterResource(DesignSystem.drawable.ic_firmware_flipper_deprecated),
                contentDescription = stringResource(R.string.control_options_dialog_title)
            )
        }
    )
}
