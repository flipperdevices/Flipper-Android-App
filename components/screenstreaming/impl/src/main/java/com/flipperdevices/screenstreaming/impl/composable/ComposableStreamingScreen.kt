package com.flipperdevices.screenstreaming.impl.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.composable.controls.ComposableFlipperControls
import com.flipperdevices.screenstreaming.impl.composable.screen.ComposableFlipperScreenWithOptions
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenshotViewModel

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

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OrangeAppBar(
            titleId = R.string.control_title,
            onBack = onBack
        )
        ComposableFlipperScreenWithOptions(
            flipperScreen = flipperScreen,
            onTakeScreenshot = { screenshotViewModel.shareScreenshot(flipperScreen) },
            modifier = Modifier.weight(1f)
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
