package com.flipperdevices.bridge.dao.impl.util

import java.io.File

internal object TempFolderProvider {
    fun provide(): File {
        val parentFolder = File.createTempFile("TEMP_FILE", ".txt")
            .parentFile
            ?: error("Could not get temp dir")
        val folderName = "TEMP_FOLDER_DEFAULT"
        val childFolder = File(parentFolder, "./$folderName")
        if (childFolder.exists()) childFolder.delete()
        childFolder.mkdirs()
        return childFolder
    }
}
