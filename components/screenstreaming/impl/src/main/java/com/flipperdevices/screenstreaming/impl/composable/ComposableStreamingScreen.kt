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

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun ComposableStreamingScreen(
    viewModel: ScreenStreamingViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val flipperScreen by viewModel.getFlipperScreen().collectAsState()

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
            onTakeScreenshot = { viewModel.shareScreenshot(flipperScreen.orientation) },
            modifier = Modifier.weight(1f)
        )
        ComposableFlipperControls(
            onPressButton = {
                viewModel.onPressButton(it, Gui.InputType.SHORT)
            },
            onLongPressButton = {
                viewModel.onPressButton(it, Gui.InputType.LONG)
            }
        )
    }
}
