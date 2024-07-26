package com.flipperdevices.bridge.connection.transport.ble.impl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.flipperdevices.bridge.connection.transport.ble.api.BleDeviceConnectionApi
import com.flipperdevices.bridge.connection.transport.ble.api.FBleApi
import com.flipperdevices.bridge.connection.transport.ble.api.FBleDeviceConnectionConfig
import com.flipperdevices.bridge.connection.transport.ble.impl.api.FBleApiImpl
import com.flipperdevices.bridge.connection.transport.ble.impl.api.FBleApiWithSerialFactory
import com.flipperdevices.bridge.connection.transport.ble.impl.model.BLEConnectionPermissionException
import com.flipperdevices.bridge.connection.transport.ble.impl.model.FailedConnectToDeviceException
import com.flipperdevices.bridge.connection.transport.ble.impl.utils.BleConstants
import com.flipperdevices.bridge.connection.transport.common.api.FInternalTransportConnectionStatus
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withTimeout
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectOptions

class BleDeviceConnectionApiImpl(
    private val context: Context,
    private val bleApiWithSerialFactory: FBleApiWithSerialFactory
) : BleDeviceConnectionApi {

    override suspend fun connect(
        scope: CoroutineScope,
        config: FBleDeviceConnectionConfig,
        listener: FTransportConnectionStatusListener
    ): Result<FBleApi> = runCatching {
        return@runCatching connectUnsafe(scope, config, listener)
    }

    private suspend fun connectUnsafe(
        scope: CoroutineScope,
        config: FBleDeviceConnectionConfig,
        listener: FTransportConnectionStatusListener
    ): FBleApi {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            throw BLEConnectionPermissionException()
        }
        listener.onStatusUpdate(FInternalTransportConnectionStatus.Connecting)
        val device = withTimeout(BleConstants.CONNECT_TIME_MS) {
            ClientBleGatt.connect(
                context = context,
                macAddress = config.macAddress,
                scope = scope,
                options = BleGattConnectOptions(
                    autoConnect = true,
                    closeOnDisconnect = false
                )
            )
        }
        if (!device.isConnected) {
            throw FailedConnectToDeviceException()
        }
        listener.onStatusUpdate(FInternalTransportConnectionStatus.Pairing)

        withTimeout(BleConstants.PAIR_TIME_MS) {
            device.waitForBonding()
        }

        device.requestMtu(BleConstants.MAX_MTU)
        device.discoverServices()

        val serialConfig = config.serialConfig
        return if (serialConfig == null) {
            FBleApiImpl(
                client = device,
                scope = scope,
                statusListener = listener,
                metaInfoGattMap = config.metaInfoGattMap
            )
        } else {
            bleApiWithSerialFactory.build(
                scope = scope,
                serialConfig = serialConfig,
                metaInfoGattMap = config.metaInfoGattMap,
                client = device,
                statusListener = listener
            )
        }
    }
}
