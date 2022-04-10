package com.flipperdevices.bridge.api.manager.service

import com.flipperdevices.bridge.api.model.FlipperVersionInformation
import kotlinx.coroutines.flow.StateFlow

interface FlipperVersionApi {
    fun getFlipperVersion(): StateFlow<FlipperVersionInformation>
}
