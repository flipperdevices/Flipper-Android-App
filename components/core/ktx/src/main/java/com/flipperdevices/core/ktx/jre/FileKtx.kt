package com.flipperdevices.core.ktx.jre

import java.io.File

fun File.createNewFileWithMkDirs(): Boolean {
    val folder = absoluteFile.parentFile
    if (folder != null && !folder.exists()) {
        val createDirectionSuccess = folder.mkdirs()
        if (!createDirectionSuccess) {
            return false
        }
    }

    return createNewFile()
}
