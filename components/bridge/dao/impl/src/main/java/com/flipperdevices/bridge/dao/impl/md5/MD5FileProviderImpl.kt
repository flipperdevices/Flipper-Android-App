package com.flipperdevices.bridge.dao.impl.md5

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.FlipperStorageProvider
import java.io.File

internal class MD5FileProviderImpl(
    private val context: Context,
    private val keyFolder: File = FlipperStorageProvider.getKeyFolder(context),
    private val md5Converter: MD5Converter,
    private val fileComparator: FileComparator
) : MD5FileProvider {
    /**
     * @return list of files with same MD5 signature
     */
    private fun getSameMd5Files(md5: String): List<File> {
        return keyFolder.listFiles()
            ?.filterNotNull()
            ?.filter { file -> file.name.contains(md5) }
            .orEmpty()
    }

    /**
     * 81731798227a5b5813d3b18a67bf133b_1
     * 4bdb3a2214c898d7463ef3b0d1aeca37_2
     * @return the index of MD5 named file
     */
    private fun getMd5FileIndex(file: File): Int {
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
    ): File? = getSameMd5Files(contentMd5).firstOrNull { sameMD5File ->
        fileComparator.isSameContent(
            keyContent.openStream(),
            sameMD5File.inputStream()
        )
    }

    private suspend fun checkMd5FileSignature(file: File, expectedMd5: String) {
        val fileMd5 = md5Converter.convert(file.inputStream())
        check(fileMd5 == expectedMd5) {
            "File $file has wrong signature (expected: $expectedMd5, actual: $fileMd5)"
        }
    }

    override suspend fun getPathToFile(contentMd5: String, keyContent: FlipperKeyContent): File {
        val sameContentFile = getSameContentFile(contentMd5, keyContent)
        if (sameContentFile != null) return sameContentFile

        val pathToFile = File(keyFolder, contentMd5)
        if (!pathToFile.exists()) return pathToFile
        verbose { "Already find file with hash $contentMd5" }
        if (BuildConfig.DEBUG) {
            checkMd5FileSignature(pathToFile, contentMd5)
        }
        val index = getMaxMD5FileIndex(contentMd5) + 1
        return File(keyFolder, contentMd5 + "_$index")
    }
}
