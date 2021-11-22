package com.flipperdevices.bridge.impl.manager.delegates

import com.flipperdevices.bridge.api.manager.delegates.FlipperConnectionInformationApi
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import kotlinx.coroutines.flow.StateFlow
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow

class FlipperConnectionInformationApiImpl(
    private val bleManager: UnsafeBleManager
) : FlipperConnectionInformationApi {
    override fun isDeviceConnected(): Boolean {
        return bleManager.isConnected
    }

    override fun getConnectionStateFlow(): StateFlow<ConnectionState> {
        return bleManager.stateAsFlow()
    }

    override fun getConnectedDeviceName(): String? {
        return bleManager.bluetoothDevice?.name
    }
}
