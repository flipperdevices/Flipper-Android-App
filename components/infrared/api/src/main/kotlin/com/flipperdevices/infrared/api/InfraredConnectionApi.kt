package com.flipperdevices.infrared.api

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import kotlinx.coroutines.flow.Flow

interface InfraredConnectionApi {
    fun getState(serviceApi: FlipperServiceApi): Flow<InfraredEmulateState>

    enum class InfraredEmulateState {
        NOT_CONNECTED, CONNECTING, SYNCING, UPDATE_FLIPPER, ALL_GOOD
    }
}
