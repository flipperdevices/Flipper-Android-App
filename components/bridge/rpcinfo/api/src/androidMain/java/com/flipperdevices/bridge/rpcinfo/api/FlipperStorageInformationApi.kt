package com.flipperdevices.bridge.rpcinfo.api

import com.flipperdevices.bridge.rpcinfo.model.FlipperStorageInformation
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FlipperStorageInformationApi {
    fun getStorageInformationFlow(): StateFlow<FlipperStorageInformation>

    suspend fun invalidate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        force: Boolean = false
    )

    suspend fun reset()
}
