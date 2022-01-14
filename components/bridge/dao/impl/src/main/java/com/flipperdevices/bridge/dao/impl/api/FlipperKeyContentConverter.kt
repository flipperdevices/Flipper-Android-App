package com.flipperdevices.bridge.dao.impl.api

import android.content.Context
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.preference.FlipperStorageProvider
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
@ContributesBinding(AppGraph::class)
class FlipperKeyContentConverter @Inject constructor(context: Context) {
    private val keyFolder = FlipperStorageProvider.getKeyFolder(context)

    suspend fun extractFile(flipperKey: FlipperKey): File = withContext(Dispatchers.IO) {
        val keyContent = flipperKey.keyContent
        if (keyContent is FlipperKeyContent.InternalFile) {
            return@withContext keyContent.file
        }

        val relativeKeyPath = File(flipperKey.fileType.flipperDir, flipperKey.name).path
        val keyFile = File(keyFolder, relativeKeyPath)

        keyContent.stream().use { inputStream ->
            keyFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return@withContext keyFile
    }
}
