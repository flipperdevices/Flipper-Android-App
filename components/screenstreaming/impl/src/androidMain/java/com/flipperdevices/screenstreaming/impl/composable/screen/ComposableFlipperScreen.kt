package com.flipperdevices.screenstreaming.impl.composable.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.composable.controls.ComposableFlipperButtonAnimation
import com.flipperdevices.screenstreaming.impl.model.FlipperButtonStackElement
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenState
import com.flipperdevices.screenstreaming.impl.viewmodel.repository.FLIPPER_SCREEN_RATIO
import com.flipperdevices.screenstreaming.impl.viewmodel.repository.ScreenStreamFrameDecoder
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.roundToInt

@Composable
fun ComposableFlipperScreen(
    buttons: ImmutableList<FlipperButtonStackElement>,
    flipperScreen: FlipperScreenState,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isHorizontal) {
            ComposableFlipperButtonAnimation(buttons)
        }

        ComposableFlipperScreenInternal(flipperScreen)

        if (isHorizontal) {
            Image(
                modifier = Modifier.padding(top = 12.dp),
                painter = painterResource(R.drawable.ic_flipper_logo),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun ComposableFlipperScreenInternal(
    flipperScreenState: FlipperScreenState
) {
    val screenColor = when (flipperScreenState) {
        FlipperScreenState.InProgress,
        is FlipperScreenState.Ready -> LocalPallet.current.flipperScreenColor

        FlipperScreenState.NotConnected -> LocalPallet.current.screenStreamingNotConnectedColor
    }
    var borderColor = LocalPallet.current.screenStreamingBorderColor

    if (!MaterialTheme.colors.isLight && flipperScreenState == FlipperScreenState.NotConnected) {
        borderColor = LocalPallet.current.screenStreamingNotConnectedColor
    }

    var boxModifier = Modifier
        .border(
            width = 3.dp,
            color = LocalPallet.current.screenStreamingBorderColor,
            shape = RoundedCornerShape(16.dp)
        )
        .padding(6.dp)
        .border(
            width = 2.dp,
            color = borderColor,
            shape = RoundedCornerShape(12.dp)
        )
        .background(
            color = screenColor,
            shape = RoundedCornerShape(12.dp)
        )
    if (flipperScreenState is FlipperScreenState.InProgress) {
        boxModifier = boxModifier
            .clip(RoundedCornerShape(12.dp))
            .placeholderConnecting()
    }
    Box(
        modifier = boxModifier
            .padding(8.dp)
    ) {
        ComposableFlipperScreenImage(flipperScreenState)
    }
}

@Composable
private fun ComposableFlipperScreenImage(flipperScreenState: FlipperScreenState) {
    val canvasModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(FLIPPER_SCREEN_RATIO)
    val canvasDescription = stringResource(R.string.flipper_display)

    when (flipperScreenState) {
        FlipperScreenState.InProgress -> Box(
            modifier = canvasModifier
        )

        FlipperScreenState.NotConnected -> Image(
            modifier = canvasModifier,
            painter = if (MaterialTheme.colors.isLight) {
                painterResource(R.drawable.pic_not_connected_light)
            } else {
                painterResource(R.drawable.pic_not_connected_dark)
            },
            contentDescription = canvasDescription
        )

        is FlipperScreenState.Ready -> ComposableFlipperScreenRaw(
            modifier = canvasModifier,
            flipperScreen = flipperScreenState.bitmap
        )
    }
}

@Composable
private fun ComposableFlipperScreenRaw(
    flipperScreen: Bitmap,
    modifier: Modifier = Modifier
) {
    val imageDrawPaint = remember {
        return@remember Paint().apply {
            filterQuality = FilterQuality.None
        }
    }
    Canvas(
        modifier = modifier,
        contentDescription = stringResource(R.string.flipper_display)
    ) {
        drawContext.canvas.drawImageRect(
            image = flipperScreen.asImageBitmap(),
            dstSize = IntSize(
                size.width.roundToInt(),
                size.height.roundToInt()
            ),
            paint = imageDrawPaint
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ComposableFlipperScreenInternalPreview() {
    FlipperThemeInternal {
        ComposableFlipperScreenInternal(FlipperScreenState.Ready(ScreenStreamFrameDecoder.emptyBitmap()))
    }
}
