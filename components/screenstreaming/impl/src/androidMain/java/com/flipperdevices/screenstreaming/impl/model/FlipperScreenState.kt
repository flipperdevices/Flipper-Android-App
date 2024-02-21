package com.flipperdevices.screenstreaming.impl.model

import android.graphics.Bitmap

sealed class FlipperScreenState {
    object NotConnected : FlipperScreenState()

    object InProgress : FlipperScreenState()
    data class Ready(
        val bitmap: Bitmap,
        val orientation: ScreenOrientationEnum = ScreenOrientationEnum.HORIZONTAL
    ) : FlipperScreenState()
}
