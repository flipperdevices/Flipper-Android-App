package com.flipperdevices.screenstreaming.impl.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.flipperdevices.core.ui.ktx.OrangeAppBar
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.composable.controls.ComposableFlipperControls
import com.flipperdevices.screenstreaming.impl.composable.screen.ComposableFlipperScreenWithOptions
import com.flipperdevices.screenstreaming.impl.model.ScreenOrientationEnum
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamingViewModel

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun ComposableStreamingScreen(
    viewModel: ScreenStreamingViewModel,
    modifier: Modifier = Modifier,
    onPressButton: (ButtonEnum) -> Unit = {},
    onLongPressButton: (ButtonEnum) -> Unit = {},
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
        var orientation by remember { mutableStateOf(ScreenOrientationEnum.HORIZONTAL) }
        ComposableFlipperScreenWithOptions(
            flipperScreen = flipperScreen.copy(orientation = orientation),
            onTakeScreenshot = {},
            modifier = Modifier.weight(1f)
        )
        Button(onClick = {
            orientation = if (orientation == ScreenOrientationEnum.HORIZONTAL) {
                ScreenOrientationEnum.VERTICAL
            } else {
                ScreenOrientationEnum.HORIZONTAL
            }
        }) {
            Text("Rotate")
        }
        ComposableFlipperControls()
    }
}
