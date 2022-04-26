package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.UpdatingState
import kotlinx.coroutines.flow.StateFlow

interface UpdaterApi {
    fun start(updateFile: DistributionFile)
    suspend fun cancel()
    fun getState(): StateFlow<UpdatingState>
}
