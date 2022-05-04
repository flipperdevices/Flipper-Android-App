package com.flipperdevices.bridge.api.manager.service

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestRpcInformationStatus
import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import kotlinx.coroutines.flow.StateFlow

interface FlipperRpcInformationApi {
    fun getRequestRpcInformationStatus(): StateFlow<FlipperRequestRpcInformationStatus>
    fun getRpcInformationFlow(): StateFlow<FlipperRpcInformation>
    suspend fun invalidate(requestApi: FlipperRequestApi)
}
