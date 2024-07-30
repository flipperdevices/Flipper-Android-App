package com.flipperdevices.core.preference

import java.io.File

object FlipperStorageProvider {
    private const val KEYS_DIR = "keysfiles/"

    fun getTemporaryFile(): File {
        val tmpDir = System.getProperty("java.io.tmpdir")
        var index = 0
        var file: File
        do {
            file = File(tmpDir, "temporaryfile-$index")
            index++
        } while (file.exists())
        file.parentFile?.mkdirs()
        file.createNewFile()
        return file
    }

    suspend fun <T> useTemporaryFile(block: suspend (File) -> T): T {
        val file = getTemporaryFile()

        val result = try {
            block(file)
        } finally {
            file.delete()
        }
        return result
    }

    suspend fun <T> useTemporaryFolder(block: suspend (File) -> T): T {
        val tmpDir = System.getProperty("java.io.tmpdir")
        var index = 0
        var folder: File
        do {
            folder = File(tmpDir, "temporaryfolder-$index")
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

    fun getAppDir(): File {
        return File(System.getProperty("user.home"), ".flipper")
    }

    fun getKeyFolder(): File {
        return File(getAppDir(), KEYS_DIR)
    }
}
