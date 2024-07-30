package com.flipperdevices.bridge.connection.feature.rpcinfo.api

import com.flipperdevices.bridge.connection.feature.common.api.FDeviceFeatureApi
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperInformationStatus
import com.flipperdevices.bridge.connection.feature.rpcinfo.model.FlipperRpcInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FRpcInfoFeatureApi : FDeviceFeatureApi {
    suspend fun invalidate(
        scope: CoroutineScope,
        force: Boolean = false
    )

    fun getRpcInformationFlow(): StateFlow<FlipperInformationStatus<FlipperRpcInformation>>

    suspend fun reset()
}
