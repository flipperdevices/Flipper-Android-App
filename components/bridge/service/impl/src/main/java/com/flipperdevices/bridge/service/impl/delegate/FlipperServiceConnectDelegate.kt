package com.flipperdevices.bridge.service.impl.delegate

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.bridge.service.impl.di.FlipperServiceComponent
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.ble.exception.BluetoothDisabledException

class FlipperServiceConnectDelegate(
    private val bleManager: FlipperBleManager,
    private val context: Context
) : LogTagProvider {
    override val TAG = "FlipperServiceConnectDelegate"

    @Inject
    lateinit var scannerProvider: Provider<FlipperScanner>

    @Inject
    lateinit var adapterProvider: Provider<BluetoothAdapter>

    init {
        ComponentHolder.component<FlipperServiceComponent>().inject(this)
    }

    private val scanner by scannerProvider
    private val adapter by adapterProvider

    suspend fun reconnect(deviceId: String) {
        // If we already connected to device, just ignore it
        if (bleManager.connectionInformationApi.isDeviceConnected()) {
            disconnect()
        }
        // If Bluetooth disable, return exception
        if (!adapter.isEnabled) {
            throw BluetoothDisabledException()
        }

        if (!PermissionHelper.isPermissionsGranted(
                context,
                PermissionHelper.getRequiredPermissions()
            )
        ) {
            throw SecurityException(
                """
                For connect to Flipper via bluetooth you need grant permission for you application. 
                Please, check PermissionHelper#checkPermissions
                """.trimIndent()
            )
        }

        // We try find device in manual mode and connect with it
        findAndConnectToDevice(deviceId)
    }

    suspend fun reconnect(device: BluetoothDevice) {
        // If we already connected to device, just ignore it
        if (bleManager.connectionInformationApi.isDeviceConnected()) {
            disconnect()
        }

        // If Bluetooth disable, return exception
        if (!adapter.isEnabled) {
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

    // All rights must be obtained before calling this method
    @SuppressLint("MissingPermission")
    private suspend fun findAndConnectToDevice(
        deviceId: String
    ) {
        var device = adapter.bondedDevices.find { it.address == deviceId }

        if (device == null) {
            device = withTimeout(Constants.BLE.CONNECT_TIME_MS) {
                scanner.findFlipperById(deviceId).first()
            }.device
        }

        bleManager.connectToDevice(device)
    }
}
