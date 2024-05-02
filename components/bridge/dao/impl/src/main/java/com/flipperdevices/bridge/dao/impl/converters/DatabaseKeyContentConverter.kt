package com.flipperdevices.bridge.dao.impl.converters

import android.content.Context
import android.os.Looper
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.comparator.FileComparator
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.core.ktx.jre.createNewFileWithMkDirs
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import com.flipperdevices.core.preference.FlipperStorageProvider
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.util.UUID

@ProvidedTypeConverter
class DatabaseKeyContentConverter(
    context: Context,
    private val md5Converter: MD5Converter,
    private val fileComparator: FileComparator,
    private val keyFolder: File = FlipperStorageProvider.getKeyFolder(context)
) : LogTagProvider {
    override val TAG = "DatabaseKeyContentConverter"

    @TypeConverter
    fun pathToKeyContent(path: String?): DatabaseKeyContent? {
        val pathNotNull = path ?: return null
        return DatabaseKeyContent(FlipperKeyContent.InternalFile(pathNotNull))
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

    @TestOnly
    suspend fun keyContentToPathInternal(keyContent: FlipperKeyContent): String {
        val contentMd5 = md5Converter.convert(keyContent.openStream())
        var pathToFile = File(keyFolder, contentMd5)
        if (pathToFile.exists()) {
            verbose { "Already find file with hash $contentMd5" }
            if (BuildConfig.DEBUG) {
                checkMd5FileSignature(pathToFile, contentMd5)
            }
            val isSameContent = fileComparator.isSameContent(
                keyContent.openStream(),
                pathToFile.inputStream()
            )
            if (isSameContent) {
                return pathToFile.absolutePath
            } else {
                pathToFile = File(keyFolder, contentMd5 + "_${UUID.randomUUID()}")
            }
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
        val fileMd5 = md5Converter.convert(file.inputStream())
        check(fileMd5 == expectedMd5) {
            "File $file has wrong signature (expected: $expectedMd5, actual: $fileMd5)"
        }
    }
}
