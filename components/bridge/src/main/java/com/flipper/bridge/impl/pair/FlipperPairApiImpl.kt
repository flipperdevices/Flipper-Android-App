package com.flipper.bridge.impl.pair

import android.content.Context
import com.flipper.bridge.api.device.FlipperDeviceApi
import com.flipper.bridge.api.pair.FlipperPairApi
import com.flipper.bridge.api.scanner.FlipperScanner
import com.flipper.bridge.impl.device.FlipperDeviceApiImpl
import com.flipper.bridge.impl.manager.FlipperBleManager
import com.flipper.bridge.utils.Constants
import com.flipper.bridge.utils.PermissionHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.ble.exception.BluetoothDisabledException
import javax.inject.Inject

class FlipperPairApiImpl @Inject constructor(
    private val scanner: FlipperScanner
) : FlipperPairApi {
    private val cachedDeviceApi: Map<String, FlipperDeviceApi> = mapOf()

    @ExperimentalCoroutinesApi
    override suspend fun connect(
        context: Context,
        deviceId: String
    ): FlipperDeviceApi {
        var deviceApi = cachedDeviceApi[deviceId]
        if (deviceApi != null) {
            return deviceApi
        }
        if (!PermissionHelper.isPermissionGranted(context)) {
            throw SecurityException(
                """
                For connect to Flipper via bluetooth you need grant permission for you application. 
                Please, check PermissionHelper#checkPermissions
                """.trimIndent()
            )
        }
        if (!PermissionHelper.isBluetoothEnabled()) {
            throw BluetoothDisabledException()
        }

        val device = withTimeout(Constants.BLE.CONNECT_TIME_MS) {
            scanner.findFlipperById(deviceId).first()
        }.device
        val manager = FlipperBleManager(context)
        manager.connect(device)
            .retry(Constants.BLE.RECONNECT_COUNT, Constants.BLE.RECONNECT_TIME_MS.toInt())
            .useAutoConnect(true)
            .enqueue()

        deviceApi = FlipperDeviceApiImpl(manager)
        return deviceApi
    }
}