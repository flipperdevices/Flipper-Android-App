package com.flipperdevices.remotecontrols.impl.grid.presentation.data.localpages

import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UpdateKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, LocalPagesRepository::class)
class LocalPagesRepositoryImpl @Inject constructor(
    private val simpleKeyApi: SimpleKeyApi,
    private val updateKeyApi: UpdateKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
) : LocalPagesRepository {

    override suspend fun save(
        flipperFilePath: FlipperFilePath,
        remotesRaw: String,
        content: FlipperKeyContent
    ) {
        val flipperKeyPath = FlipperKeyPath(
            path = flipperFilePath,
            deleted = false
        )
        val additionalFiles = listOf(
            FlipperFile(
                path = flipperFilePath,
                content = content
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

    override suspend fun delete(path: FlipperFilePath) {
        deleteKeyApi.markDeleted(path)
        deleteKeyApi.deleteMarkedDeleted(path)
    }

    override suspend fun getLocalFlipperKey(path: FlipperFilePath): FlipperKey? {
        return simpleKeyApi.getKey(
            FlipperKeyPath(
                path = path,
                deleted = false
            )
        )
    }

    override suspend fun getLocalPagesLayout(
        path: FlipperFilePath,
        toPagesLayout: (String) -> PagesLayout?
    ): PagesLayout? {
        val flipperKey = getLocalFlipperKey(path)
        return flipperKey
            .let { it?.additionalFiles.orEmpty() }
            .let { additionalFiles ->
                additionalFiles.firstNotNullOfOrNull { fFile ->
                    val text = fFile.content.openStream().reader().readText()
                    toPagesLayout.invoke(text)
                }
            }
    }
}
