package com.flipperdevices.firstpair.impl.viewmodels.connecting

import android.bluetooth.BluetoothGatt
import android.content.Context
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import no.nordicsemi.android.ble.BleManager

/**
 * Class for initial connection to device. Set up connection and set up pairing
 */
internal class FirstPairBleManager(context: Context) : BleManager(context), LogTagProvider {
    override val TAG = "FirstPairBleManager"

    private val gattCallback = FirstPairBleManagerGattCallback()

    override fun getGattCallback(): BleManagerGattCallback = gattCallback

    private inner class FirstPairBleManagerGattCallback :
        BleManagerGattCallback() {
        override fun initialize() {
            if (!isBonded) {
                info { "Start bond secure" }
                ensureBond().enqueue()
            }
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt) = true

        override fun onServicesInvalidated() = Unit
    }
}
