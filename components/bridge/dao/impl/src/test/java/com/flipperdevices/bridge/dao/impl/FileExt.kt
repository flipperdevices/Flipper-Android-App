package com.flipperdevices.bridge.dao.impl

import java.io.File
import java.util.UUID

internal object FileExt {
    private const val TXT_EXTENSION_WITH_DOT = ".txt"

    const val DEFAULT_TEXT = "DEFAULT_TEXT"

    val RANDOM_FILE_NAME: String
        get() = UUID.randomUUID().toString()

    const val STUB_CONTENT = "STUB_CONTENT"

    val RANDOM_CONTENT: String
        get() = RANDOM_FILE_NAME

    const val STUB_MD5: String = "STUB_MD5"

    val RANDOM_MD5: String
        get() = RANDOM_FILE_NAME

    val tempDir: File
        get() = File.createTempFile(RANDOM_FILE_NAME, TXT_EXTENSION_WITH_DOT)
            .parentFile ?: error("Could not get temp dir")

    fun File.child(other: String) = File(this, other)

    fun getRandomFolder(): File {
        val parentFolder = tempDir
        val folderName = UUID.randomUUID().toString()
        val childFolder = parentFolder.child("./$folderName")
        if (childFolder.exists()) childFolder.delete()
        childFolder.mkdirs()
        return childFolder
    }

    /**
     * Create temp file filled with [content]
     */
    fun createFilledFile(content: String): File {
        val parentFolder = getRandomFolder()
        val fileName = UUID.randomUUID().toString()
        return File(parentFolder, fileName).apply {
            writeText(content)
        }
    }
}
