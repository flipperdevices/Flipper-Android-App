package com.flipperdevices.screenstreaming.impl.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.set
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.screen.Gui
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenSnapshot
import com.flipperdevices.screenstreaming.impl.model.ScreenOrientationEnum
import kotlin.experimental.and

private const val SCREEN_WIDTH = 128
private const val SCREEN_HEIGHT = 64
const val FLIPPER_SCREEN_RATIO = (SCREEN_WIDTH / SCREEN_HEIGHT).toFloat()
private const val BACKGROUND_COLOR = -0x73d7 // 0xFFFF8C29
private const val SINGLE_BIT = 1
private const val ZERO_BYTE = 0.toByte()
private const val PIXEL_MASK = 7

object ScreenStreamFrameDecoder : LogTagProvider {
    override val TAG = "ScreenStreamFrameDecoder"
    fun emptyBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(BACKGROUND_COLOR)
        return bitmap
    }

    fun decode(streamFrame: Gui.ScreenFrame): FlipperScreenSnapshot? {
        val bytes = streamFrame.data.toByteArray()
        info { "Receive package with ${bytes.size} bytes" }
        if (bytes.isEmpty()) {
            return null
        }
        val orientation = when (streamFrame.orientation) {
            Gui.ScreenOrientation.HORIZONTAL -> ScreenOrientationEnum.HORIZONTAL
            Gui.ScreenOrientation.HORIZONTAL_FLIP -> ScreenOrientationEnum.HORIZONTAL_FLIP
            Gui.ScreenOrientation.VERTICAL -> ScreenOrientationEnum.VERTICAL
            Gui.ScreenOrientation.VERTICAL_FLIP -> ScreenOrientationEnum.VERTICAL_FLIP
            else -> ScreenOrientationEnum.HORIZONTAL
        }
        val screen = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888)
        fillBitmap(screen, bytes, orientation)
        return FlipperScreenSnapshot(
            bitmap = screen,
            orientation = orientation
        )
    }

    private fun fillBitmap(screen: Bitmap, bytes: ByteArray, orientation: ScreenOrientationEnum) {
        for (x in 0 until SCREEN_WIDTH) {
            for (y in 0 until SCREEN_HEIGHT) {
                val color = if (bytes.isPixelSet(x, y)) Color.BLACK else BACKGROUND_COLOR
                val bitmapX = when (orientation) {
                    ScreenOrientationEnum.VERTICAL_FLIP,
                    ScreenOrientationEnum.HORIZONTAL_FLIP -> SCREEN_WIDTH - x - 1
                    else -> x
                }
                val bitmapY = when (orientation) {
                    ScreenOrientationEnum.HORIZONTAL_FLIP,
                    ScreenOrientationEnum.VERTICAL_FLIP -> SCREEN_HEIGHT - y - 1
                    else -> y
                }
                screen[bitmapX, bitmapY] = color
            }
        }
    }

    private fun ByteArray.isPixelSet(x: Int, y: Int): Boolean {
        val index = (y / Byte.SIZE_BITS) * SCREEN_WIDTH + x
        val modifiedY = y and PIXEL_MASK
        return get(index) and (SINGLE_BIT.shl(modifiedY).toByte()) != ZERO_BYTE
    }
}
