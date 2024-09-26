package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.rpc.api.model.NameWithHash
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.DetailedProgressListener
import com.flipperdevices.core.progress.DetailedProgressWrapperTracker
import com.flipperdevices.protobuf.storage.Storage
import com.squareup.anvil.annotations.ContributesBinding
import java.io.File
import javax.inject.Inject

const val SIZE_BYTES_LIMIT = 10 * 1024 * 1024 // 10MiB

interface FlipperHashRepository {
    data class HashesProgressDetail(
        val keyType: FlipperKeyType
    ) : DetailedProgressListener.Detail

    suspend fun getHashesForType(
        keyType: FlipperKeyType,
        tracker: DetailedProgressWrapperTracker
    ): List<KeyWithHash>
}

@ContributesBinding(TaskGraph::class, FlipperHashRepository::class)
class FlipperHashRepositoryImpl @Inject constructor(
    private val flipperStorageApi: FlipperStorageApi
) : FlipperHashRepository, LogTagProvider {
    override val TAG = "HashRepository"

    override suspend fun getHashesForType(
        keyType: FlipperKeyType,
        tracker: DetailedProgressWrapperTracker
    ): List<KeyWithHash> {
        tracker.onProgress(
            current = 0f,
            detail = FlipperHashRepository.HashesProgressDetail(keyType)
        )
        val fileTypePath = File(Constants.KEYS_DEFAULT_STORAGE, keyType.flipperDir).path
        val files = flipperStorageApi.listingDirectoryWithMd5(fileTypePath)
        info { "Receive ${files.size} files" }
        tracker.onProgress(
            current = 1f,
            detail = FlipperHashRepository.HashesProgressDetail(keyType)
        )
        return files.filter { isValidFile(it, keyType) }.map {
            KeyWithHash(keyPath = FlipperFilePath(keyType.flipperDir, it.name), hash = it.md5)
        }
    }

    private fun isValidFile(nameWithHash: NameWithHash, requestedType: FlipperKeyType): Boolean {
        if (nameWithHash.type != Storage.File.FileType.FILE) {
            debug {
                "File ${nameWithHash.name} is not file. This is folder. Ignore it"
            }
            return false
        }
        if (nameWithHash.size > SIZE_BYTES_LIMIT) {
            debug {
                "File ${nameWithHash.name} skip," +
                    " because current size limit is $SIZE_BYTES_LIMIT, " +
                    "but file size is ${nameWithHash.size}"
            }
            return false
        }
        if (nameWithHash.name.startsWith(".")) {
            debug {
                "File ${nameWithHash.name} skip, because it starts with dot"
            }
            return false
        }
        val extension = nameWithHash.name.substringAfterLast(".")

        if (FlipperFileType.getByExtension(extension) == FlipperFileType.SHADOW_NFC &&
            requestedType == FlipperKeyType.NFC
        ) {
            return true
        }
        if (FlipperFileType.getByExtension(extension) == FlipperFileType.UI_INFRARED &&
            requestedType == FlipperKeyType.INFRARED
        ) {
            return true
        }
        val fileTypeByExtension = FlipperKeyType.getByExtension(extension)
        if (fileTypeByExtension == null) {
            debug {
                "File ${nameWithHash.name} skip, because we don't support this file extension ($extension)"
            }
            return false
        }
        if (fileTypeByExtension != requestedType) {
            debug {
                "File ${nameWithHash.name} skip, because folder type ($requestedType) " +
                    "and extension type ($fileTypeByExtension) is not equals"
            }
            return false
        }
        return true
    }
}
