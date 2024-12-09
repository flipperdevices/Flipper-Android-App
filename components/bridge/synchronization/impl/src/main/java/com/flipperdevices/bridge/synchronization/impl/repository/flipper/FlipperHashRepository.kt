package com.flipperdevices.bridge.synchronization.impl.repository.flipper

import com.flipperdevices.bridge.connection.feature.storage.api.fm.FListingStorageApi
import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.bridge.connection.feature.storage.api.model.ListingItemWithHash
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.synchronization.impl.di.TaskGraph
import com.flipperdevices.bridge.synchronization.impl.model.KeyWithHash
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.debug
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.progress.DetailedProgressListener
import com.flipperdevices.core.progress.DetailedProgressWrapperTracker
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlin.io.path.Path

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
    private val flipperStorageApi: FListingStorageApi
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

        val fileTypePath = Path("/ext/").resolve(keyType.flipperDir).toString()
        val files = flipperStorageApi.lsWithMd5(fileTypePath)
            .onFailure { error(it) { "#getHashesForType could not get directories" } }
            .getOrNull()
            .orEmpty()
        info { "Receive ${files.size} files" }
        tracker.onProgress(
            current = 1f,
            detail = FlipperHashRepository.HashesProgressDetail(keyType)
        )
        return files.filter { isValidFile(it, keyType) }.map {
            KeyWithHash(keyPath = FlipperFilePath(keyType.flipperDir, it.fileName), hash = it.md5)
        }
    }

    private fun isValidFile(nameWithHash: ListingItemWithHash, requestedType: FlipperKeyType): Boolean {
        if (nameWithHash.fileType != FileType.FILE) {
            debug {
                "File ${nameWithHash.fileName} is not file. This is folder. Ignore it"
            }
            return false
        }
        if (nameWithHash.size > SIZE_BYTES_LIMIT) {
            debug {
                "File ${nameWithHash.fileName} skip," +
                    " because current size limit is $SIZE_BYTES_LIMIT, " +
                    "but file size is ${nameWithHash.size}"
            }
            return false
        }
        if (nameWithHash.fileName.startsWith(".")) {
            debug {
                "File ${nameWithHash.fileName} skip, because it starts with dot"
            }
            return false
        }
        val extension = nameWithHash.fileName.substringAfterLast(".")

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
                "File ${nameWithHash.fileName} skip, because we don't support this file extension ($extension)"
            }
            return false
        }
        if (fileTypeByExtension != requestedType) {
            debug {
                "File ${nameWithHash.fileName} skip, because folder type ($requestedType) " +
                    "and extension type ($fileTypeByExtension) is not equals"
            }
            return false
        }
        return true
    }
}
