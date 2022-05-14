package com.flipperdevices.analytics.shake2report.impl

import android.graphics.Bitmap
import java.io.File

interface InternalShake2Report {
    val logDir: File

    fun register()
    fun getScreenshotAndReset(): Bitmap?
    fun setExtra(tags: List<Pair<String, String>>)
}
