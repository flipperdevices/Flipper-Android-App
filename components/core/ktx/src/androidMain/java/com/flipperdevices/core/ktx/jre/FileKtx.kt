package com.flipperdevices.core.ktx.jre

import java.io.File
import java.nio.charset.Charset

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

fun File.createClearNewFileWithMkDirs(): Boolean {
    if (exists()) {
        delete()
    }

    return createNewFileWithMkDirs()
}

fun File.readBytes(limit: Long): ByteArray {
    return inputStream().use {
        BoundedInputStream(it, limit).readBytes()
    }
}

fun File.readText(limit: Long, charset: Charset = Charsets.UTF_8): String {
    return String(readBytes(limit), charset)
}
