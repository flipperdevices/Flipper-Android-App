package com.flipperdevices.core.ktx.android

import android.content.Context
import java.io.File

fun Context.createClearFileInCacheSafe(dir: String, fileName: String): File {
    val directory = File(cacheDir, dir)
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val targetFile = File(directory, fileName)
    if (targetFile.exists()) {
        targetFile.delete()
    }
    return targetFile
}
