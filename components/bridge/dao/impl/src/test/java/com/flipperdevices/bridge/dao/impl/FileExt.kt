package com.flipperdevices.bridge.dao.impl

import java.io.File
import java.util.UUID

internal object FileExt {

    fun provideTempFolder(): File {
        val parentFolder = File.createTempFile("TEMP_FILE", ".txt")
            .parentFile
            ?: error("Could not get temp dir")
        val folderName = UUID.randomUUID().toString()
        val childFolder = File(parentFolder, "./$folderName")
        if (childFolder.exists()) childFolder.delete()
        childFolder.mkdirs()
        return childFolder
    }

    /**
     * Create temp file filled with [content]
     */
    fun createFilledFile(content: String, dir: File): File {
        val fileName = UUID.randomUUID().toString()
        return File(dir, fileName).apply {
            writeText(content)
        }
    }
}
