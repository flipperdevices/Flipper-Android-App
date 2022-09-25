package com.flipperdevices.updater.api

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.updater.model.FlipperUpdateState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UpdateStateApi {
    fun getFlipperUpdateState(
        serviceApi: FlipperServiceApi,
        scope: CoroutineScope
    ): Flow<FlipperUpdateState>
}
