package com.flipperdevices.bridge.dao.impl.api

import com.flipperdevices.bridge.dao.api.delegates.FlipperFileApi
import com.flipperdevices.bridge.dao.api.model.FlipperFile
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyContent
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.impl.ktx.toDatabaseFile
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
        val extension = when (file.path.fileType) {
            FlipperFileType.SHADOW_NFC -> FlipperKeyType.NFC.extension
            FlipperFileType.UI_INFRARED -> FlipperKeyType.INFRARED.extension
            else -> {
                error("There is support only for NFC shadow and INFRARED UI files at the moment")
            }
        }
        val pathToKeyFile = FlipperFilePath(
            folder = file.path.folder,
            nameWithExtension = "${file.path.nameWithoutExtension}.$extension"
        )
        val foundedKey = simpleKeyDao.getByPath(pathToKeyFile.pathToKey, deleted = false)
            ?: error("Can't find nfc or infrared key for ${file.path}")

        val faf = FlipperAdditionalFile(
            path = file.path.pathToKey,
            content = DatabaseKeyContent(file.content),
            keyId = foundedKey.uid
        )
        additionalFileDao.insert(faf)
    }

    override suspend fun updateAdditionalFiles(
        id: Int,
        newAdditionalFiles: List<FlipperFile>
    ) {
        val additionalFilesToRemove = additionalFileDao.getFilesForKeyWithId(id)
            .map { it.path to it }.toMap(HashMap())
        newAdditionalFiles.forEach {
            val additionalFile = it.toDatabaseFile(id)
            val existedFile = additionalFilesToRemove.remove(additionalFile.path)
            if (existedFile != null) {
                additionalFileDao.update(additionalFile.copy(uid = existedFile.uid))
            } else {
                additionalFileDao.insert(additionalFile)
            }
        }
        additionalFilesToRemove.values.forEach {
            additionalFileDao.delete(it)
        }
    }

    override suspend fun renameAdditionalFiles(id: Int, newKey: FlipperKey) {
        val newName = newKey.path.nameWithoutExtension
        val newAdditionalFiles = additionalFileDao
            .getFilesForKeyWithId(id)
            .map {
                val path = it.filePath
                it.copy(
                    path = path.copy(
                        nameWithExtension = "$newName.${path.extension}"
                    ).pathToKey
                )
            }
        newAdditionalFiles.forEach {
            additionalFileDao.update(it)
        }
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
