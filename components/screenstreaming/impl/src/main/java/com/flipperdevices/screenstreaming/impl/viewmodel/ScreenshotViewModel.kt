package com.flipperdevices.screenstreaming.impl.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import com.flipperdevices.core.ktx.jre.createClearNewFileWithMkDirs
import com.flipperdevices.core.share.SharableFile
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.screenstreaming.impl.R
import com.flipperdevices.screenstreaming.impl.model.FlipperScreenState
import com.flipperdevices.screenstreaming.impl.model.ScreenOrientationEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private const val SCREENSHOT_FILE_PREFIX = "flpr"
private const val TIMEFORMAT = "yyyy-MM-dd-HH:mm:ss"
private const val QUALITY = 100
private const val EXPORT_RESCALE_MULTIPLIER = 8f

class ScreenshotViewModel @Inject constructor(
    private val application: Application
) : DecomposeViewModel() {
    fun shareScreenshot(
        screenShapshot: FlipperScreenState
    ) = viewModelScope.launch(Dispatchers.Default) {
        val currentSnapshotState = screenShapshot as? FlipperScreenState.Ready ?: return@launch
        var currentSnapshot = currentSnapshotState.bitmap
        currentSnapshot = currentSnapshot.rescale()
        currentSnapshot = when (screenShapshot.orientation) {
            ScreenOrientationEnum.VERTICAL,
            ScreenOrientationEnum.VERTICAL_FLIP -> currentSnapshot.rotate(angel = 90f)
            ScreenOrientationEnum.HORIZONTAL,
            ScreenOrientationEnum.HORIZONTAL_FLIP -> currentSnapshot
        }
        val date = SimpleDateFormat(TIMEFORMAT, Locale.US).format(Date())
        val filename = "$SCREENSHOT_FILE_PREFIX-$date.png"
        val sharableFile = SharableFile(application, filename)
        sharableFile.createClearNewFileWithMkDirs()
        sharableFile.outputStream().use {
            currentSnapshot.compress(Bitmap.CompressFormat.PNG, QUALITY, it)
        }
        ShareHelper.shareFile(application, sharableFile, R.string.screenshot_export_title)
    }
}

private fun Bitmap.rescale(): Bitmap {
    val matrix = Matrix()
    matrix.postScale(EXPORT_RESCALE_MULTIPLIER, EXPORT_RESCALE_MULTIPLIER)
    return Bitmap.createBitmap(
        this,
        0,
        0,
        width,
        height,
        matrix,
        false
    )
}

private fun Bitmap.rotate(angel: Float): Bitmap {
    val matrix = Matrix()

    matrix.postRotate(angel)

    val scaledBitmap = Bitmap.createScaledBitmap(this, width, height, true)

    return Bitmap.createBitmap(
        scaledBitmap,
        0,
        0,
        scaledBitmap.width,
        scaledBitmap.height,
        matrix,
        true
    )
}
