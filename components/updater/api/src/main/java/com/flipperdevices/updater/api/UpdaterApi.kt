package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.model.UpdatingStateWithRequest
import kotlinx.coroutines.flow.StateFlow

interface UpdaterApi {
    fun isUpdateInProcess(): Boolean
    fun getState(): StateFlow<UpdatingStateWithRequest>
    fun resetState()

    fun onDeviceConnected(versionName: FirmwareVersion)

    suspend fun start(updateRequest: UpdateRequest)
    suspend fun cancel(silent: Boolean = false)
}
