package com.flipperdevices.bridge.dao.impl.md5

import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.verbose
import com.squareup.anvil.annotations.ContributesBinding
import okio.Path
import okio.buffer
import okio.source
import okio.use
import javax.inject.Inject

@ContributesBinding(AppGraph::class, MD5FileProvider::class)
class MD5FileProviderImpl @Inject constructor(
    private val fileComparator: FileComparator,
    private val storageProvider: FlipperStorageProvider
) : MD5FileProvider {
    private val keyFolder = storageProvider.getKeyFolder()

    /**
     * @return list of files with same MD5 signature
     */
    private fun getSameMd5Files(md5: String): List<Path> {
        return runCatching { storageProvider.fileSystem.list(keyFolder) }
            .getOrNull()
            ?.filter { file -> file.name.startsWith(md5) }
            .orEmpty()
    }

    /**
     * 81731798227a5b5813d3b18a67bf133b_1
     * 4bdb3a2214c898d7463ef3b0d1aeca37_2
     * @return the index of MD5 named file
     */
    private fun getMd5FileIndex(file: Path): Int {
        if (!file.name.contains("_")) return 0
        return file.name.split("_").last().toIntOrNull() ?: 0
    }

    private fun getMaxMD5FileIndex(md5: String): Int {
        return getSameMd5Files(md5).maxOfOrNull(::getMd5FileIndex) ?: 0
    }

    /**
     * @return the first file with the same content and MD5 signature
     */
    private suspend fun getSameContentFile(
        contentMd5: String,
        keyContent: FlipperKeyContent
    ): Path? = getSameMd5Files(contentMd5).firstOrNull { sameMD5File ->
        keyContent.openStream().source().buffer().use { keyContentSource ->
            storageProvider.fileSystem.source(sameMD5File).buffer().use { fileSource ->
                fileComparator.isSameContent(
                    keyContentSource,
                    fileSource
                )
            }
        }
    }

    override suspend fun getPathToFile(contentMd5: String, keyContent: FlipperKeyContent): Path {
        val pathToFile = keyFolder.resolve(contentMd5)
        if (!storageProvider.fileSystem.exists(pathToFile)) return pathToFile

        val sameContentFile = getSameContentFile(contentMd5, keyContent)
        if (sameContentFile != null) {
            verbose { "Already find file with hash $contentMd5" }
            return sameContentFile
        }

        verbose { "Already find file with hash $contentMd5" }
        val index = getMaxMD5FileIndex(contentMd5) + 1
        return keyFolder.resolve("${contentMd5}_$index")
    }
}
