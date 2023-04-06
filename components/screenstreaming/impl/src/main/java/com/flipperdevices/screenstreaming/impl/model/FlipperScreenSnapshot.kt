package com.flipperdevices.screenstreaming.impl.model

import android.graphics.Bitmap
import com.flipperdevices.screenstreaming.impl.viewmodel.ScreenStreamFrameDecoder

data class FlipperScreenSnapshot(
    val bitmap: Bitmap = ScreenStreamFrameDecoder.emptyBitmap(),
    val orientation: ScreenOrientationEnum = ScreenOrientationEnum.HORIZONTAL
)