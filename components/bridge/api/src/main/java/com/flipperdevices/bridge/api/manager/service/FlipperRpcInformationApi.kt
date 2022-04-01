package com.flipperdevices.bridge.api.manager.service

import com.flipperdevices.bridge.api.model.FlipperRpcInformation
import kotlinx.coroutines.flow.StateFlow

interface FlipperRpcInformationApi {
    fun getRpcInformationFlow(): StateFlow<FlipperRpcInformation>
}
