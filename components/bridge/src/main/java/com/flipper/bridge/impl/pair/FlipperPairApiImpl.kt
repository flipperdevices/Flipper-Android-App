package com.flipper.bridge.impl.pair

import android.bluetooth.BluetoothDevice
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

    override fun getFlipperApi(
        context: Context,
        deviceId: String
    ): FlipperDeviceApi {
        val deviceApi = cachedDeviceApi[deviceId]
        if (deviceApi != null) {
            return deviceApi
        }

        val manager = FlipperBleManager(context)
        return FlipperDeviceApiImpl(manager, deviceId)
    }

    @ExperimentalCoroutinesApi
    override suspend fun connect(
        context: Context,
        flipperDeviceApi: FlipperDeviceApi
    ) {
        if (flipperDeviceApi.getBleManager().isConnected) {
            return
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
            scanner.findFlipperById(flipperDeviceApi.address).first()
        }.device

        scheduleConnect(flipperDeviceApi, device)
    }

    override fun scheduleConnect(flipperDeviceApi: FlipperDeviceApi, device: BluetoothDevice) {
        if (flipperDeviceApi.getBleManager().isConnected) {
            return
        }
        flipperDeviceApi.getBleManager().connect(device)
            .retry(Constants.BLE.RECONNECT_COUNT, Constants.BLE.RECONNECT_TIME_MS.toInt())
            .useAutoConnect(false)
            .enqueue()
    }
}
