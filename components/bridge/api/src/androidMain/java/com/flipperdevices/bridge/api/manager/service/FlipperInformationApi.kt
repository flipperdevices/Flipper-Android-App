package com.flipperdevices.bridge.api.manager.service

import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import kotlinx.coroutines.flow.StateFlow

interface FlipperInformationApi {
    fun getInformationFlow(): StateFlow<FlipperGATTInformation>
}
