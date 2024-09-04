package com.flipperdevices.keyedit.api

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.preference.FlipperStorageProvider
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class NotSavedFlipperKey(
    val mainFile: NotSavedFlipperFile,
    val additionalFiles: List<NotSavedFlipperFile>,
    val notes: String?
)

@Serializable
data class NotSavedFlipperFile(
    val path: FlipperFilePath,
    val content: FlipperKeyContent
)

suspend fun FlipperFile.toNotSavedFlipperFile(
    context: Context
): NotSavedFlipperFile = withContext(FlipperDispatchers.workStealingDispatcher) {
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
    return@withContext NotSavedFlipperFile(
        path,
        FlipperKeyContent.InternalFile(internalFile.absolutePath)
    )
}
