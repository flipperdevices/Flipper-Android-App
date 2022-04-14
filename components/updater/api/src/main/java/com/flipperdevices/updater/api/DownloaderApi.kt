package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.VersionFiles
import java.util.EnumMap

interface DownloaderApi {
    suspend fun getLatestVersion(): EnumMap<FirmwareChannel, VersionFiles>
}
