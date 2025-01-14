package com.flipperdevices.infrared.api

import kotlinx.coroutines.flow.Flow

interface InfraredConnectionApi {
    fun getState(): Flow<InfraredEmulateState>

    enum class InfraredEmulateState {
        NOT_CONNECTED, CONNECTING, SYNCING, UPDATE_FLIPPER, ALL_GOOD
    }
}
