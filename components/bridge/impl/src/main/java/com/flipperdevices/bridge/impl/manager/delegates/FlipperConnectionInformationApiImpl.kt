package com.flipperdevices.bridge.impl.manager.delegates

import android.annotation.SuppressLint
import com.flipperdevices.bridge.api.manager.delegates.FlipperConnectionInformationApi
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.api.manager.ktx.stateAsFlow
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import kotlinx.coroutines.flow.Flow

class FlipperConnectionInformationApiImpl(
    private val bleManager: UnsafeBleManager
) : FlipperConnectionInformationApi {
    override fun isDeviceConnected(): Boolean {
        return bleManager.isConnected
    }

    override fun getConnectionStateFlow(): Flow<ConnectionState> {
        return bleManager.stateAsFlow()
    }

    @SuppressLint("MissingPermission")
    override fun getConnectedDeviceName(): String? {
        return bleManager.bluetoothDevice?.name
    }
}
