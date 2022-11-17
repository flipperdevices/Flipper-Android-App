package com.flipperdevices.keyedit.api

import android.content.Context
import android.os.Parcelable
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.preference.FlipperStorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotSavedFlipperKey(
    val mainFile: NotSavedFlipperFile,
    val additionalFiles: List<NotSavedFlipperFile>,
    val notes: String?
) : Parcelable

@Parcelize
data class NotSavedFlipperFile(
    val path: FlipperFilePath,
    val content: FlipperKeyContent.InternalFile
) : Parcelable

suspend fun FlipperFile.toNotSavedFlipperFile(
    context: Context
): NotSavedFlipperFile = withContext(Dispatchers.Default) {
    val localContent = content
    if (localContent is FlipperKeyContent.InternalFile) {
        return@withContext NotSavedFlipperFile(path, localContent)
    }
    val internalFile = FlipperStorageProvider.getTemporaryFile(context)
    localContent.openStream().use { contentStream ->
        internalFile.outputStream().use { fileStream ->
            contentStream.copyTo(fileStream)
        }
    }
    return@withContext NotSavedFlipperFile(path, FlipperKeyContent.InternalFile(internalFile))
}
