package com.flipperdevices.bridge.dao.impl.api.delegates

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
class FlipperKeyContentConverterImpl @Inject constructor(
    context: Context
) : FlipperKeyContentConverter {
    private val keyFolder = FlipperStorageProvider.getKeyFolder(context)

    override suspend fun extractFile(flipperKey: FlipperKey) = withContext(Dispatchers.IO) {
        val keyContent = flipperKey.keyContent
        if (keyContent is FlipperKeyContent.InternalFile) {
            return@withContext keyContent.file
        }

        val keyFile = File(keyFolder, flipperKey.path.pathToKey)

        keyContent.stream().use { inputStream ->
            keyFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return@withContext keyFile
    }
}
