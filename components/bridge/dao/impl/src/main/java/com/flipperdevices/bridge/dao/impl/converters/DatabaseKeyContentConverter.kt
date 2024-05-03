package com.flipperdevices.bridge.dao.impl.converters

import android.os.Looper
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.md5.MD5Converter
import com.flipperdevices.bridge.dao.impl.md5.MD5FileProvider
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.core.ktx.jre.createNewFileWithMkDirs
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import javax.inject.Inject

@ProvidedTypeConverter
class DatabaseKeyContentConverter @Inject constructor(
    private val md5Converter: MD5Converter,
    private val mD5FileProvider: MD5FileProvider,
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

    private suspend fun keyContentToPathInternal(keyContent: FlipperKeyContent): String {
        val contentMd5 = md5Converter.convert(keyContent.openStream())

        val pathToFile = mD5FileProvider.getPathToFile(contentMd5, keyContent)
        if (pathToFile.exists()) return pathToFile.absolutePath

        pathToFile.createNewFileWithMkDirs()
        verbose { "Create new file with hash $contentMd5" }
        pathToFile.outputStream().use { fileStream ->
            keyContent.openStream().use { contentStream ->
                contentStream.copyTo(fileStream)
            }
        }

        return pathToFile.absolutePath
    }
}
