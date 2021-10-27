package com.flipperdevices.bridge.api.manager.delegates

import kotlinx.coroutines.flow.StateFlow
import no.nordicsemi.android.ble.ktx.state.ConnectionState

interface FlipperConnectionInformationApi {
    fun isDeviceConnected(): Boolean
    fun getConnectionStateFlow(): StateFlow<ConnectionState>
}
