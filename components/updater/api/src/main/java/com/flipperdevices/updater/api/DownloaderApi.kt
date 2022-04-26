package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.DownloadProgress
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.VersionFiles
import java.io.File
import java.util.EnumMap
import kotlinx.coroutines.flow.Flow

interface DownloaderApi {
    suspend fun getLatestVersion(): EnumMap<FirmwareChannel, VersionFiles>

    fun download(
        distributionFile: DistributionFile,
        target: File,
        decompress: Boolean = false
    ): Flow<DownloadProgress>
}
