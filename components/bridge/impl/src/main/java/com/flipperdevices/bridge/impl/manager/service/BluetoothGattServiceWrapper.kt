package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGattService
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager

/**
 * Delegate interface for wrap gatt services
 */
interface BluetoothGattServiceWrapper {
    /**
     * Call when device notify about supported service
     */
    fun onServiceReceived(service: BluetoothGattService)

    /**
     * Call when device is ready for reading characteristics
     * Call after {@link #onServiceReceived}
     */
    fun initialize(bleManager: UnsafeBleManager)

    /**
     * Reset stateflows and others stateful component
     * Calls after reconnect to new device or invalidate services
     */
    fun reset(bleManager: UnsafeBleManager)
}
