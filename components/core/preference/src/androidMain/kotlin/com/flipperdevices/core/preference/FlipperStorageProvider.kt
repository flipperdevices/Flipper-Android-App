package com.flipperdevices.core.preference

import android.content.Context
import java.io.File

object FlipperStorageProvider {
    private const val KEYS_DIR = "keysfiles/"

    fun getTemporaryFile(context: Context): File {
        var index = 0
        var file: File
        do {
            file = File(context.cacheDir, "temporaryfile-$index")
            index++
        } while (file.exists())
        file.parentFile?.mkdirs()
        file.createNewFile()
        return file
    }

    suspend fun <T> useTemporaryFile(context: Context, block: suspend (File) -> T): T {
        val file = getTemporaryFile(context)

        val result = try {
            block(file)
        } finally {
            file.delete()
        }
        return result
    }

    suspend fun <T> useTemporaryFolder(context: Context, block: suspend (File) -> T): T {
        var index = 0
        var folder: File
        do {
            folder = File(context.cacheDir, "temporaryfolder-$index")
            index++
        } while (folder.exists())
        folder.mkdirs()

        val result = try {
            block(folder)
        } finally {
            folder.deleteRecursively()
        }
        return result
    }

    fun getKeyFolder(context: Context): File {
        return File(context.filesDir, KEYS_DIR)
    }
}
