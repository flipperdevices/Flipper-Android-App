package com.flipperdevices.keyedit.impl.api

import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.FlipperStorageProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.keyedit.api.NotSavedFlipperFile
import com.flipperdevices.keyedit.api.NotSavedFlipperKeyApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ContributesBinding(AppGraph::class, NotSavedFlipperKeyApi::class)
class NotSavedFlipperKeyApiImpl @Inject constructor(
    private val storageProvider: FlipperStorageProvider
) : NotSavedFlipperKeyApi {
    override suspend fun toNotSavedFlipperFile(
        flipperFile: FlipperFile
    ): NotSavedFlipperFile = withContext(FlipperDispatchers.workStealingDispatcher) {
        val localContent = flipperFile.content
        if (localContent is FlipperKeyContent.InternalFile) {
            return@withContext NotSavedFlipperFile(flipperFile.path, localContent)
        }
        val internalFile = storageProvider.getTemporaryFile().toFile()
        localContent.openStream().use { contentStream ->
            internalFile.outputStream().use { fileStream ->
                contentStream.copyTo(fileStream)
            }
        }
        return@withContext NotSavedFlipperFile(
            flipperFile.path,
            FlipperKeyContent.InternalFile(internalFile.absolutePath)
        )
    }
}
