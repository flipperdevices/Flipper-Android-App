package com.flipperdevices.bridge.dao.impl.converters

import android.content.Context
import android.os.Looper
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.core.ktx.jre.createNewFileWithMkDirs
import com.flipperdevices.core.ktx.jre.md5
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.FlipperStorageProvider
import java.io.File

@ProvidedTypeConverter
class DatabaseKeyContentConverter(context: Context) : LogTagProvider {
    override val TAG = "DatabaseKeyContentConverter"

    private val keyFolder = FlipperStorageProvider.getKeyFolder(context)

    @TypeConverter
    fun pathToKeyContent(path: String?): DatabaseKeyContent? {
        val pathNotNull = path ?: return null
        return DatabaseKeyContent(FlipperKeyContent.InternalFile(File(pathNotNull)))
    }

    @TypeConverter
    fun keyContentToPath(keyContent: DatabaseKeyContent?): String? {
        if (BuildConfig.INTERNAL && Looper.getMainLooper() == Looper.myLooper()) {
            error("This method can be executed only on background thread!")
        }

        val keyContentNotNull = keyContent?.flipperContent ?: return null

        return runBlockingWithLog("convert") {
            keyContentToPathInternal(keyContentNotNull)
        }
    }

    private suspend fun keyContentToPathInternal(keyContent: FlipperKeyContent): String {
        val contentMd5 = keyContent.openStream().md5()
        val pathToFile = File(keyFolder, contentMd5)
        if (pathToFile.exists()) {
            verbose { "Already find file with hash $contentMd5" }
            if (BuildConfig.DEBUG) {
                checkMd5FileSignature(pathToFile, contentMd5)
            }
            return pathToFile.absolutePath
        }
        pathToFile.createNewFileWithMkDirs()
        verbose { "Create new file with hash $contentMd5" }
        pathToFile.outputStream().use { fileStream ->
            keyContent.openStream().use { contentStream ->
                contentStream.copyTo(fileStream)
            }
        }

        return pathToFile.absolutePath
    }

    private suspend fun checkMd5FileSignature(file: File, expectedMd5: String) {
        val fileMd5 = file.inputStream().md5()
        check(fileMd5 == expectedMd5) {
            "File $file has wrong signature (expected: $expectedMd5, actual: $fileMd5)"
        }
    }
}
