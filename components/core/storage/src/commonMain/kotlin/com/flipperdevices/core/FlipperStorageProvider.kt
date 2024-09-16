package com.flipperdevices.core

import okio.FileSystem
import okio.Path

private const val KEYS_DIR = "keysfiles"

abstract class FlipperStorageProvider {
    abstract val fileSystem: FileSystem
    protected abstract val tmpPath: Path
    abstract val rootPath: Path

    fun getTemporaryFile(): Path {
        var index = 0
        var file: Path
        do {
            file = tmpPath.resolve("temporaryfile-$index")
            index++
        } while (fileSystem.exists(file))
        val parentFile = file.parent
        if (parentFile != null) {
            runCatching { fileSystem.createDirectory(parentFile) }
        }
        return file
    }

    suspend fun <T> useTemporaryFile(block: suspend (Path) -> T): T {
        val file = getTemporaryFile()

        val result = try {
            block(file)
        } finally {
            fileSystem.delete(file)
        }
        return result
    }

    suspend fun <T> useTemporaryFolder(block: suspend (Path) -> T): T {
        var index = 0
        var folder: Path
        do {
            folder = tmpPath.resolve("temporaryfolder-$index")
            index++
        } while (fileSystem.exists(folder))

        fileSystem.createDirectory(folder)

        val result = try {
            block(folder)
        } finally {
            fileSystem.deleteRecursively(folder)
        }
        return result
    }

    fun getKeyFolder() = rootPath.resolve(KEYS_DIR)

    fun mkdirsParent(path: Path) {
        path.parent?.let { fileSystem.createDirectories(it) }
    }
}
