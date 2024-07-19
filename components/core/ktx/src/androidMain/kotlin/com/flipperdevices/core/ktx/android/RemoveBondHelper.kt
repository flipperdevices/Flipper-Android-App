package com.flipperdevices.core.ktx.android

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info

object RemoveBondHelper : LogTagProvider {
    override val TAG = "RemoveBondHelper"

    @SuppressLint("MissingPermission")
    fun removeBond(adapter: BluetoothAdapter, id: String): Boolean {
        info { "Request remove bond for $id" }
        val pairedDevices = adapter.bondedDevices.filter { it.address == id }
        if (pairedDevices.isEmpty()) {
            info { "Return false because no any paired device with id $id" }
            return false // Not found any paired devices
        }
        info { "Found ${pairedDevices.size} paired devices with id $id" }

        var isSuccess = false
        for (device in pairedDevices) {
            val result = removeBond(device).onFailure {
                error(it) { "Failed remove bond for device with id $id and device is $device" }
            }
            if (result.getOrNull() == true) {
                info { "Remove bond successful for $device" }
                isSuccess = true
            }
        }
        return isSuccess // At lease one bond was deleted
    }

    fun removeBond(device: BluetoothDevice): Result<Boolean> = runCatching {
        info { "Request remove bond for $device" }
        val removeBond = device.javaClass.getMethod("removeBond")
        return@runCatching removeBond.invoke(device) as Boolean
    }
}
