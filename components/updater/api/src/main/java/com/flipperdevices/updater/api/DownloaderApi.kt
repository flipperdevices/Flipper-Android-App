package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.DownloadProgress
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.SubGhzProvisioningException
import com.flipperdevices.updater.model.SubGhzProvisioningModel
import com.flipperdevices.updater.model.VersionFiles
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.EnumMap

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
