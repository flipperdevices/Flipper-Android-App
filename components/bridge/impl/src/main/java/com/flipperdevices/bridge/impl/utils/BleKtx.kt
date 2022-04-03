package com.flipperdevices.bridge.impl.utils

import android.bluetooth.BluetoothGatt
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.bridge.impl.manager.service.BluetoothGattServiceWrapper

fun BluetoothGattServiceWrapper.initializeSafe(
    manager: UnsafeBleManager,
    onError: (Throwable?) -> Unit
) {
    runCatching {
        initialize(manager)
    }.onFailure {
        onError(it)
    }
}

fun BluetoothGattServiceWrapper.onServiceReceivedSafe(
    gatt: BluetoothGatt,
    onError: (Throwable?) -> Unit
) {
    runCatching {
        onServiceReceived(gatt)
    }.onSuccess { serviceReceived ->
        if (!serviceReceived) {
            onError(null)
        }
    }.onFailure {
        onError(it)
    }
}
