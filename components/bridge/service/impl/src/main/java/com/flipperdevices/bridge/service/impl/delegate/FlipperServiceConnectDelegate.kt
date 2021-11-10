package com.flipperdevices.bridge.service.impl.delegate

import android.bluetooth.BluetoothDevice
import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.api.utils.DeviceFeatureHelper
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.bridge.provider.FlipperApi
import com.flipperdevices.bridge.service.impl.di.FlipperServiceComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.ble.exception.BluetoothDisabledException

class FlipperServiceConnectDelegate(
    private val bleManager: FlipperBleManager,
    private val context: Context
) : LogTagProvider {
    override val TAG = "FlipperServiceConnectDelegate"

    private val scanner = FlipperApi.flipperScanner
    private val adapter = FlipperApi.bluetoothAdapter

    init {
        ComponentHolder.component<FlipperServiceComponent>().inject(this)
    }

    suspend fun reconnect(deviceId: String) {
        // If we already connected to device, just ignore it
        if (bleManager.connectionInformationApi.isDeviceConnected()) {
            disconnect()
        }
        // If Bluetooth disable, return exception
        if (!PermissionHelper.isBluetoothEnabled()) {
            throw BluetoothDisabledException()
        }

        // If we use companion feature, we can't connect without bonded device
        if (DeviceFeatureHelper.isCompanionFeatureAvailable(context)) {
            connectWithBondedDevice(deviceId)
            return
        }
        // If companion feature not available, we try find device in manual mode and connect with it
        findAndConnectToDevice(context, deviceId)
    }

    suspend fun reconnect(device: BluetoothDevice) {
        // If we already connected to device, just ignore it
        if (bleManager.connectionInformationApi.isDeviceConnected()) {
            disconnect()
        }

        // If Bluetooth disable, return exception
        if (!PermissionHelper.isBluetoothEnabled()) {
            throw BluetoothDisabledException()
        }

        bleManager.connectToDevice(device)
    }

    suspend fun disconnect() {
        try {
            withTimeout(Constants.BLE.DISCONNECT_TIMEOUT_MS) {
                bleManager.disconnectDevice()
            }
        } catch (timeout: TimeoutCancellationException) {
            error(timeout.cause) {
                "Can't disconnect device with timeout ${Constants.BLE.DISCONNECT_TIMEOUT_MS}"
            }
        }
    }

    private suspend fun connectWithBondedDevice(deviceId: String) {
        val device = adapter.bondedDevices.find { it.address == deviceId }
            ?: throw IllegalArgumentException("Can't find bonded device with this id")
        bleManager.connectToDevice(device)
    }

    private suspend fun findAndConnectToDevice(
        context: Context,
        deviceId: String
    ) {
        if (!PermissionHelper.isPermissionGranted(context)) {
            throw SecurityException(
                """
                For connect to Flipper via bluetooth you need grant permission for you application. 
                Please, check PermissionHelper#checkPermissions
                """.trimIndent()
            )
        }

        val device = withTimeout(Constants.BLE.CONNECT_TIME_MS) {
            scanner.findFlipperById(deviceId).first()
        }.device

        bleManager.connectToDevice(device)
    }
}
