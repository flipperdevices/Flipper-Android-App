package com.flipperdevices.updater.api

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.updater.model.FirmwareVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface FlipperVersionProviderApi {
    fun getCurrentFlipperVersion(
        coroutineScope: CoroutineScope,
        serviceApi: FlipperServiceApi
    ): StateFlow<FirmwareVersion?>
}
