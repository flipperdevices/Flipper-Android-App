package com.flipperdevices.bridge.rpcinfo.api

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.rpcinfo.model.FlipperStorageInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FlipperStorageInformationApi {
    fun getStorageInformationFlow(): StateFlow<FlipperStorageInformation>

    suspend fun invalidate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi,
        force: Boolean = false
    )

    suspend fun reset()
}