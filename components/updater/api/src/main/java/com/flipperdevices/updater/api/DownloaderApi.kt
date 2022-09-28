package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.DownloadProgress
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.SubGhzProvisioningException
import com.flipperdevices.updater.model.SubGhzProvisioningModel
import com.flipperdevices.updater.model.VersionFiles
import java.io.File
import java.util.EnumMap
import kotlinx.coroutines.flow.Flow

interface DownloaderApi {
    suspend fun getLatestVersion(): EnumMap<FirmwareChannel, VersionFiles>

    @Throws(SubGhzProvisioningException::class)
    suspend fun getSubGhzProvisioning(): SubGhzProvisioningModel

    fun download(
        distributionFile: DistributionFile,
        target: File,
        decompress: Boolean = false
    ): Flow<DownloadProgress>
}

interface DownloadAndUnpackDelegate {
    suspend fun download(
        distributionFile: DistributionFile,
        target: File,
        onProgress: (suspend (Long, Long) -> Unit)? = null
    )

    suspend fun unpack(source: File, target: File)
}
