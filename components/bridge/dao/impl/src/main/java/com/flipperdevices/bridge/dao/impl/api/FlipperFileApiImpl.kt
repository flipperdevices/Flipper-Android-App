package com.flipperdevices.bridge.dao.impl.api

import com.flipperdevices.bridge.dao.api.delegates.FlipperFileApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.impl.ktx.toFlipperFile
import com.flipperdevices.bridge.dao.impl.model.DatabaseKeyContent
import com.flipperdevices.bridge.dao.impl.model.FlipperAdditionalFile
import com.flipperdevices.bridge.dao.impl.repository.AdditionalFileDao
import com.flipperdevices.bridge.dao.impl.repository.key.SimpleKeyDao
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider

@ContributesBinding(AppGraph::class, FlipperFileApi::class)
class FlipperFileApiImpl @Inject constructor(
    simpleKeyDaoProvider: Provider<SimpleKeyDao>,
    additionalFileDaoProvider: Provider<AdditionalFileDao>
) : FlipperFileApi, LogTagProvider {
    override val TAG = "FlipperFileApi"

    private val additionalFileDao by additionalFileDaoProvider
    private val simpleKeyDao by simpleKeyDaoProvider

    override suspend fun getFile(filePath: FlipperFilePath): FlipperFile {
        return additionalFileDao.getByPath(filePath.pathToKey, keyDeleted = false)?.toFlipperFile()
            ?: error("Can't find additional file $filePath")
    }

    override suspend fun insert(file: FlipperFile) {
        if (file.path.fileType != FlipperFileType.SHADOW_NFC) {
            error("There is support only for NFC shadow files at the moment")
        }
        val pathToKeyFile = FlipperFilePath(
            folder = file.path.folder,
            nameWithExtension = "${file.path.nameWithoutExtension}.${FlipperKeyType.NFC.extension}"
        )
        val foundedKey = simpleKeyDao.getByPath(pathToKeyFile.pathToKey, deleted = false)
            ?: error("Can't find nfc key for ${file.path}")

        val faf = FlipperAdditionalFile(
            path = file.path.pathToKey,
            content = DatabaseKeyContent(file.content),
            keyId = foundedKey.uid
        )
        additionalFileDao.insert(faf)
    }

    override suspend fun updateFileContent(
        filePath: FlipperFilePath,
        newContent: FlipperKeyContent
    ) {
        val additionalFile = additionalFileDao.getByPath(filePath.pathToKey, keyDeleted = false)
            ?: error("Can't find file $filePath")
        val newFile = additionalFile.copy(
            content = DatabaseKeyContent(newContent)
        )
        additionalFileDao.update(newFile)
    }

    override suspend fun deleteFile(filePath: FlipperFilePath) {
        val additionalFile = additionalFileDao.getByPath(filePath.pathToKey, keyDeleted = false)
            ?: return
        additionalFileDao.delete(additionalFile)
    }
}
