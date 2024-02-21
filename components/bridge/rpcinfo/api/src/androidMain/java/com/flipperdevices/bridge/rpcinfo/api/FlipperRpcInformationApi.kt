package com.flipperdevices.bridge.rpcinfo.api

import com.flipperdevices.bridge.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.rpcinfo.model.FlipperRpcInformation
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FlipperRpcInformationApi {
    fun getRpcInformationFlow(): StateFlow<FlipperInformationStatus<FlipperRpcInformation>>

    suspend fun invalidate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        force: Boolean = false
    )

    suspend fun reset()
}
