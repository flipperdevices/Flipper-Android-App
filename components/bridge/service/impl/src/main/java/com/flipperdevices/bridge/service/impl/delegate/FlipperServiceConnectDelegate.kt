package com.flipperdevices.bridge.service.impl.delegate

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.api.utils.DeviceFeatureHelper
import com.flipperdevices.bridge.api.utils.PermissionHelper
import com.flipperdevices.bridge.service.impl.di.FlipperServiceComponent
import com.flipperdevices.core.di.ComponentHolder
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.ble.exception.BluetoothDisabledException

class FlipperServiceConnectDelegate(
    private val bleManager: FlipperBleManager,
    private val context: Context
) {
    @Inject
    lateinit var scanner: FlipperScanner

    @Inject
    lateinit var adapter: BluetoothAdapter

    init {
        ComponentHolder.component<FlipperServiceComponent>().inject(this)
    }

    suspend fun reconnect(deviceId: String) {
        // If we already connected to device, just ignore it
        if (bleManager.isDeviceConnected) {
            bleManager.disconnectDevice()
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
