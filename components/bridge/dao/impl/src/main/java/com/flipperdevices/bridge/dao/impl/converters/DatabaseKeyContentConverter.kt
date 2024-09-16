package com.flipperdevices.bridge.dao.impl.converters

import android.os.Looper
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.impl.md5.MD5Converter
import com.flipperdevices.bridge.dao.impl.md5.MD5FileProvider
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.BuildConfig
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.verbose
import okio.buffer
import okio.source
import javax.inject.Inject

@ProvidedTypeConverter
class DatabaseKeyContentConverter @Inject constructor(
    private val md5Converter: MD5Converter,
    private val mD5FileProvider: MD5FileProvider,
    private val flipperStorageProvider: FlipperStorageProvider
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
        if (flipperStorageProvider.fileSystem.exists(pathToFile)) {
            return pathToFile.normalized().toString()
        }

        flipperStorageProvider.mkdirsParent(pathToFile)
        verbose { "Create new file with hash $contentMd5" }

        flipperStorageProvider.fileSystem.sink(pathToFile).buffer().use { fileSink ->
            keyContent.openStream().source().buffer().use { contentSource ->
                fileSink.writeAll(contentSource)
            }
        }

        return pathToFile.normalized().toString()
    }
}
