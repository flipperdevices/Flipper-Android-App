package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import java.util.UUID

/**
 * Delegate interface for wrap gatt services
 */
interface BluetoothGattServiceWrapper {
    /**
     * Call when device notify about supported service
     * @return true if requested service is present
     */
    fun onServiceReceived(gatt: BluetoothGatt): Boolean

    /**
     * Call when device is ready for reading characteristics
     * Call after {@link #onServiceReceived}
     */
    suspend fun initialize(bleManager: UnsafeBleManager)

    /**
     * Reset stateflows and others stateful component
     * Calls after reconnect to new device or invalidate services
     */
    suspend fun reset(bleManager: UnsafeBleManager)
}

fun LogTagProvider.getServiceOrLog(gatt: BluetoothGatt, uuid: UUID): BluetoothGattService? {
    val service = gatt.getService(uuid)
    if (service == null) {
        error { "Can't find service with UUID: $uuid" }
        return null
    }
    return service
}

fun LogTagProvider.getCharacteristicOrLog(
    service: BluetoothGattService,
    uuid: UUID
): BluetoothGattCharacteristic? {
    val characteristic = service.getCharacteristic(uuid)
    if (characteristic == null) {
        error { "Can't find characteristic with UUID: $uuid in service ${service.uuid}" }
        return null
    }
    return characteristic
}
