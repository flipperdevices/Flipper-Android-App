package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import com.flipperdevices.bridge.api.manager.service.RestartRPCApi
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info

class RestartRPCApiImpl : RestartRPCApi, BluetoothGattServiceWrapper, LogTagProvider {
    override val TAG = "RestartRPCApi"

    private var bleManagerInternal: UnsafeBleManager? = null

    private var rpcStateCharacteristic: BluetoothGattCharacteristic? = null

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        val service = getServiceOrLog(
            gatt = gatt,
            uuid = Constants.BLESerialService.SERVICE_UUID
        ) ?: return false

        rpcStateCharacteristic = getCharacteristicOrLog(
            service,
            Constants.BLESerialService.RPC_STATE
        )

        return rpcStateCharacteristic != null
    }

    override fun restartRpc() {
        info { "Request restart rpc" }
        bleManagerInternal?.writeCharacteristicUnsafe(rpcStateCharacteristic, byteArrayOf(0))
            ?.enqueue()
    }

    override suspend fun initialize(bleManager: UnsafeBleManager) {
        bleManagerInternal = bleManager
    }

    override suspend fun reset(bleManager: UnsafeBleManager) = Unit
}
