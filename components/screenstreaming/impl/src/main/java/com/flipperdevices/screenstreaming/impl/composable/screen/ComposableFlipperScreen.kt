package com.flipperdevices.screenstreaming.impl.composable.screen

import com.flipperdevices.core.ui.res.R as DesignSystem
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.viewmodel.FLIPPER_SCREEN_RATIO
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamFrameDecoder
import kotlin.math.roundToInt

@Composable
fun ComposableFlipperScreen(
    bitmap: Bitmap,
    showLogo: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ComposableFlipperScreenInternal(bitmap)

        if (showLogo) {
            Image(
                modifier = Modifier.padding(top = 12.dp),
                painter = painterResource(DesignSystem.drawable.ic_flipper_logo),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun ComposableFlipperScreenInternal(flipperScreen: Bitmap) {
    Box(
        Modifier
            .border(
                width = 3.dp,
                color = LocalPallet.current.screenStreamingBorderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(6.dp)
            .border(
                width = 2.dp,
                color = LocalPallet.current.screenStreamingBorderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = LocalPallet.current.flipperScreenColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        ComposableFlipperScreenRaw(flipperScreen)
    }
}

@Composable
private fun ComposableFlipperScreenRaw(flipperScreen: Bitmap) {
    val imageDrawPaint = remember {
        Paint().apply {
            filterQuality = FilterQuality.None
        }
    }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(FLIPPER_SCREEN_RATIO),
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
        ComposableFlipperScreenInternal(ScreenStreamFrameDecoder.emptyBitmap())
    }
}
