package com.flipperdevices.core.ktx.android

import android.graphics.Bitmap
import android.graphics.Matrix

object BitmapKtx {
    fun Bitmap.rescale(scale: Float, filter: Boolean = false): Bitmap {
        return rescale(scaleX = scale, scaleY = scale, filter = filter)
    }

    fun Bitmap.rescale(scaleX: Float, scaleY: Float, filter: Boolean = false): Bitmap {
        val matrix = Matrix()
        matrix.postScale(scaleX, scaleY)
        return Bitmap.createBitmap(
            this,
            0,
            0,
            width,
            height,
            matrix,
            filter
        )
    }
}
