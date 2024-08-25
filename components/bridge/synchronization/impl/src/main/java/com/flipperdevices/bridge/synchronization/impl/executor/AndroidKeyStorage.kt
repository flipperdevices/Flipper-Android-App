package com.flipperdevices.bridge.synchronization.impl.executor

import com.flipperdevices.bridge.dao.api.delegates.FlipperFileApi
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@StorageType(Platform.ANDROID)
@ContributesBinding(TaskGraph::class, AbstractKeyStorage::class)
class AndroidKeyStorage @Inject constructor(
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
    private val flipperFileApi: FlipperFileApi
) : AbstractKeyStorage, LogTagProvider {
    override val TAG = "AndroidKeyStorage"

    override suspend fun loadFile(filePath: FlipperFilePath): FlipperKeyContent {
        info { "Load key $filePath" }
        return when (filePath.fileType) {
            FlipperFileType.KEY -> simpleKeyApi.getKey(
                FlipperKeyPath(filePath, deleted = false)
            )?.mainFile?.content ?: error("Can't found $filePath")

            FlipperFileType.SHADOW_NFC,
            FlipperFileType.UI_INFRARED -> flipperFileApi.getFile(filePath).content

            FlipperFileType.OTHER ->
                error("I cannot process a file that is neither a key nor a shadow file: $filePath")
        }
    }

    override suspend fun modify(
        filePath: FlipperFilePath,
        newContent: FlipperKeyContent
    ) {
        info { "Modify key $filePath" }
        when (filePath.fileType) {
            FlipperFileType.KEY -> simpleKeyApi.updateKeyContent(
                FlipperKeyPath(
                    filePath,
                    deleted = false
                ),
                newContent
            )

            FlipperFileType.UI_INFRARED,
            FlipperFileType.SHADOW_NFC ->
                flipperFileApi.updateFileContent(filePath, newContent)

            FlipperFileType.OTHER ->
                error("I cannot process a file that is neither a key nor a shadow file: $filePath")
        }
    }

    override suspend fun saveFile(filePath: FlipperFilePath, keyContent: FlipperKeyContent) {
        info { "Save key $filePath with ${keyContent.length()} bytes" }
        when (filePath.fileType) {
            FlipperFileType.KEY -> simpleKeyApi.insertKey(
                FlipperKey(
                    mainFile = FlipperFile(
                        filePath,
                        keyContent
                    ),
                    synchronized = true,
                    deleted = false
                )
            )

            FlipperFileType.UI_INFRARED,
            FlipperFileType.SHADOW_NFC -> flipperFileApi.insert(FlipperFile(filePath, keyContent))

            FlipperFileType.OTHER ->
                error("I cannot process a file that is neither a key nor a shadow file: $filePath")
        }
    }

    override suspend fun deleteFile(filePath: FlipperFilePath) {
        info { "Mark delete key $filePath" }
        when (filePath.fileType) {
            FlipperFileType.KEY -> deleteKeyApi.markDeleted(filePath)
            FlipperFileType.UI_INFRARED,
            FlipperFileType.SHADOW_NFC -> flipperFileApi.deleteFile(filePath)

            FlipperFileType.OTHER ->
                error("I cannot process a file that is neither a key nor a shadow file: $filePath")
        }
    }
}
