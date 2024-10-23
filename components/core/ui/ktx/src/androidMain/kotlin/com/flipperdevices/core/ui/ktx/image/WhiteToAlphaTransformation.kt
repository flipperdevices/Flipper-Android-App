package com.flipperdevices.core.ui.ktx.image

import android.graphics.Bitmap
import android.graphics.Color
import coil3.size.Size
import coil3.transform.Transformation

class WhiteToAlphaTransformation : Transformation() {
    override val cacheKey: String = javaClass.name

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val newBitmap = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)
        for (w in 0 until input.width) {
            for (h in 0 until input.height) {
                val color = input.getPixel(w, h)
                val newColor = if (color == Color.WHITE) {
                    Color.TRANSPARENT
                } else {
                    color
                }
                newBitmap.setPixel(w, h, newColor)
            }
        }
        return newBitmap
    }
}
