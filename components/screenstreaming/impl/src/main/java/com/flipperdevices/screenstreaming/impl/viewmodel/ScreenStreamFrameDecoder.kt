package com.flipperdevices.screenstreaming.impl.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.set
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.screen.Gui
import kotlin.experimental.and

private const val SCREEN_WIDTH = 128
private const val SCREEN_HEIGHT = 64
const val FLIPPER_SCREEN_RATIO = (SCREEN_WIDTH / SCREEN_HEIGHT).toFloat()
private const val BACKGROUND_COLOR = -0x73d7 // 0xFFFF8C29

object ScreenStreamFrameDecoder : LogTagProvider {
    override val TAG = "ScreenStreamFrameDecoder"
    fun emptyBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(BACKGROUND_COLOR)
        return bitmap
    }

    fun decode(streamFrame: Gui.ScreenStreamFrame): Bitmap {
        val bytes = streamFrame.data.toByteArray()
        info { "Receive package with ${bytes.size} bytes" }
        if (bytes.isEmpty()) {
            return emptyBitmap()
        }
        val screen = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888)
        for (x in 0 until SCREEN_WIDTH) {
            for (y in 0 until SCREEN_HEIGHT) {
                val color = if (bytes.isPixelSet(x, y)) Color.BLACK else BACKGROUND_COLOR
                screen[x, y] = color
            }
        }
        return screen
    }

    private fun ByteArray.isPixelSet(x: Int, y: Int): Boolean {
        var index = (y / Byte.SIZE_BITS) * SCREEN_WIDTH
        val modifiedY = y and 7
        index += x
        return get(index) and (1.shl(modifiedY).toByte()) != 0.toByte()
    }
}
