package com.flipperdevices.screenstreaming.impl.composable

import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.model.StreamingState
import com.flipperdevices.screenstreaming.impl.viewmodel.FLIPPER_SCREEN_RATIO
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamFrameDecoder

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalComposeUiApi
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableScreen(
    flipperScreen: Bitmap = ScreenStreamFrameDecoder.emptyBitmap(),
    streamingState: StreamingState = StreamingState.DISABLED,
    onPressButton: (ButtonEnum) -> Unit = {},
    onLongPressButton: (ButtonEnum) -> Unit = {},
    onScreenStreamingSwitch: (StreamingState) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(FLIPPER_SCREEN_RATIO),
            bitmap = flipperScreen.asImageBitmap(),
            contentDescription = stringResource(R.string.flipper_display)
        )
        ComposableControlButtons(onPressButton, onLongPressButton)
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onScreenStreamingSwitch(
                    when (streamingState) {
                        StreamingState.DISABLED -> StreamingState.ENABLED
                        StreamingState.ENABLED -> StreamingState.DISABLED
                    }
                )
            }
        ) {
            Text(
                text = stringResource(
                    id = when (streamingState) {
                        StreamingState.DISABLED -> R.string.screen_streaming_enable
                        StreamingState.ENABLED -> R.string.screen_streaming_disable
                    }
                ),
            )
        }
    }
}
