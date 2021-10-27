package com.flipperdevices.bridge.api.manager

import android.bluetooth.BluetoothDevice
import com.flipperdevices.bridge.api.manager.service.FlipperInformationApi
import kotlinx.coroutines.flow.StateFlow
import no.nordicsemi.android.ble.ktx.state.ConnectionState

interface FlipperBleManager : FlipperSerialApi {
    val isDeviceConnected: Boolean

    fun getConnectionStateFlow(): StateFlow<ConnectionState>

    // This section provide access to device apis
    val informationApi: FlipperInformationApi
    val flipperRequestApi: FlipperRequestApi

    /**
     * Connect to device {@param device}
     * Await while disconnect process is not finish
     */
    suspend fun connectToDevice(device: BluetoothDevice)

    /**
     * Disconnect from current device
     * Await while disconnect process is not finish
     */
    suspend fun disconnectDevice()
}
