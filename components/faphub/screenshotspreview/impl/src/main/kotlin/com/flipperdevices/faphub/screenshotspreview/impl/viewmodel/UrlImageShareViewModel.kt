package com.flipperdevices.faphub.screenshotspreview.impl.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import com.flipperdevices.core.ktx.android.BitmapKtx.rescale
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.createClearNewFileWithMkDirs
import com.flipperdevices.core.share.SharableFile
import com.flipperdevices.core.share.ShareHelper
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.screenshotspreview.impl.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val SCREENSHOT_FILE_PREFIX = "flpr"
private const val TIMEFORMAT = "yyyy-MM-dd-HH-mm-ss"
private const val QUALITY = 100
private const val EXPORT_RESCALE_MULTIPLIER = 8f

class UrlImageShareViewModel @Inject constructor(
    private val applicationContext: Context
) : DecomposeViewModel() {

    private suspend fun shareScreenshot(bitmap: Bitmap) {
        val date = SimpleDateFormat(TIMEFORMAT, Locale.US).format(Date())
        val filename = "$SCREENSHOT_FILE_PREFIX-$date.png"
        val sharableFile = SharableFile(applicationContext, filename)
        sharableFile.createClearNewFileWithMkDirs()
        sharableFile.outputStream().use { fis ->
            bitmap.compress(Bitmap.CompressFormat.PNG, QUALITY, fis)
        }
        applicationContext.getColor(com.flipperdevices.core.ui.res.R.color.accent)
        withContext(Dispatchers.Main) {
            ShareHelper.shareFile(
                applicationContext,
                sharableFile,
                R.string.screenshotspreview_export_title
            )
        }
    }

    private fun URL.decodeBitmap(): Result<Bitmap> {
        return kotlin.runCatching {
            openStream().use(BitmapFactory::decodeStream)
        }
    }

    // Images have transparent background thus we need to fill it with accent color
    private fun Bitmap.fillBackground(): Bitmap {
        val newBitmap = Bitmap.createBitmap(width, height, config ?: Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(applicationContext.getColor(DesignSystem.color.accent))
        val noBlurPaint = Paint().apply {
            this.isAntiAlias = false
            this.isFilterBitmap = false
        }
        canvas.drawBitmap(this, 0F, 0F, noBlurPaint)
        recycle()
        return newBitmap
    }

    fun shareUrlImage(url: URL) = viewModelScope.launch(FlipperDispatchers.workStealingDispatcher) {
        url.decodeBitmap()
            .map { bitmap -> bitmap.fillBackground() }
            .map { bitmap -> bitmap.rescale(EXPORT_RESCALE_MULTIPLIER) }
            .onSuccess { bitmap -> shareScreenshot(bitmap) }
    }
}
