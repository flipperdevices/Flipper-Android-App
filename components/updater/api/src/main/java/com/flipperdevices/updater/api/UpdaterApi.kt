package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.UpdatingStateWithVersion
import com.flipperdevices.updater.model.VersionFiles
import kotlinx.coroutines.flow.StateFlow

interface UpdaterApi {
    fun isUpdateInProcess(): Boolean
    fun getState(): StateFlow<UpdatingStateWithVersion>

    fun onDeviceConnected()
    fun start(versionFiles: VersionFiles)
    suspend fun cancel()
}
