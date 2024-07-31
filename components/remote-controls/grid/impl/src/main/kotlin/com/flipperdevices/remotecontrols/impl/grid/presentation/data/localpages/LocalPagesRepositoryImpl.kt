package com.flipperdevices.remotecontrols.impl.grid.presentation.data.localpages

import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@ContributesBinding(AppGraph::class, LocalPagesRepository::class)
class LocalPagesRepositoryImpl @Inject constructor(
    private val simpleKeyApi: SimpleKeyApi,
    private val updateKeyApi: UpdateKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
) : LocalPagesRepository {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }

    override suspend fun save(
        ifrFileId: Long,
        remotesRaw: String,
        pagesLayout: PagesLayout
    ) {
        val flipperFilePath = FlipperFilePath(
            folder = "${FlipperKeyType.INFRARED.flipperDir}/temp/$ifrFileId",
            nameWithExtension = "$ifrFileId.ir"
        )
        val flipperKeyPath = FlipperKeyPath(
            path = flipperFilePath,
            deleted = false
        )
        val additionalFiles = listOf(
            FlipperFile(
                path = FlipperFilePath(
                    folder = "/temp/$ifrFileId",
                    nameWithExtension = "template.ui.json"
                ),
                content = FlipperKeyContent.RawData(
                    json.encodeToString(pagesLayout).toByteArray()
                )
            )
        )
        val existingKey = simpleKeyApi.getKey(flipperKeyPath)
        val key = existingKey ?: FlipperKey(
            mainFile = FlipperFile(
                path = flipperFilePath,
                content = FlipperKeyContent.RawData(remotesRaw.toByteArray())
            ),
            synchronized = true,
            deleted = false,
        )
        if (existingKey == null) simpleKeyApi.insertKey(key = key)
        updateKeyApi.updateKey(key, key.copy(additionalFiles = additionalFiles))
    }

    override suspend fun delete(ifrFileId: Long) {
        val flipperFilePath = FlipperFilePath(
            folder = "${FlipperKeyType.INFRARED.flipperDir}/temp/$ifrFileId",
            nameWithExtension = "$ifrFileId.ir"
        )
        deleteKeyApi.markDeleted(flipperFilePath)
        deleteKeyApi.deleteMarkedDeleted(flipperFilePath)
    }

    override suspend fun getLocalFlipperKey(ifrFileId: Long): FlipperKey? {
        return simpleKeyApi.getKey(
            FlipperKeyPath(
                path = FlipperFilePath(
                    folder = "${FlipperKeyType.INFRARED.flipperDir}/temp/$ifrFileId",
                    nameWithExtension = "$ifrFileId.ir"
                ),
                deleted = false
            )
        )
    }

    override suspend fun getLocalPagesLayout(ifrFileId: Long): PagesLayout? {
        val flipperKey = getLocalFlipperKey(ifrFileId)
        return flipperKey
            .let { it?.additionalFiles.orEmpty() }
            .let { additionalFiles ->
                additionalFiles.firstNotNullOfOrNull { fFile ->
                    runCatching {
                        val text = fFile.content.openStream().reader().readText()
                        json.decodeFromString<PagesLayout>(text)
                    }.getOrNull()
                }
            }
    }
}
