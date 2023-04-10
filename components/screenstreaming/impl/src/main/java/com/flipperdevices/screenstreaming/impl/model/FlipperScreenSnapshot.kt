package com.flipperdevices.screenstreaming.impl.model

import android.graphics.Bitmap

data class FlipperScreenSnapshot(
    val bitmap: Bitmap? = null,
    val orientation: ScreenOrientationEnum = ScreenOrientationEnum.HORIZONTAL
)
