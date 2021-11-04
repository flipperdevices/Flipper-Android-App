package com.flipperdevices.screenstreaming.impl.composable

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.viewmodel.FLIPPER_SCREEN_RATIO
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamFrameDecoder

@ExperimentalComposeUiApi
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableScreen(
    flipperScreen: Bitmap = ScreenStreamFrameDecoder.emptyBitmap(),
    onPressButton: (ButtonEnum) -> Unit = {}
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
        ComposableControlButtons(onPressButton)
    }
}
