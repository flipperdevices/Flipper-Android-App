package com.flipperdevices.bridge.api.manager

import android.bluetooth.BluetoothDevice
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import kotlinx.coroutines.flow.StateFlow
import no.nordicsemi.android.ble.ktx.state.ConnectionState

interface FlipperBleManager : FlipperSerialApi {
    val isDeviceConnected: Boolean
    val flipperRequestApi: FlipperRequestApi
    fun getInformationStateFlow(): StateFlow<FlipperGATTInformation>
    fun getConnectionStateFlow(): StateFlow<ConnectionState>

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
