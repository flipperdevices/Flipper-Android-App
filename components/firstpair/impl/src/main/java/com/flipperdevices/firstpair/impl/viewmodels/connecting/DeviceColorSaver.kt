package com.flipperdevices.firstpair.impl.viewmodels.connecting

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.scanner.DiscoveredBluetoothDevice
import com.flipperdevices.core.preference.pb.HardwareColor
import com.flipperdevices.core.preference.pb.PairSettings
import java.util.UUID
import javax.inject.Inject

private val TRANSPARENT_UUID = UUID.fromString("00003083-0000-1000-8000-00805f9b34fb")
private val WHITE_UUID = UUID.fromString("00003082-0000-1000-8000-00805f9b34fb")
private val BLACK_UUID = UUID.fromString("00003081-0000-1000-8000-00805f9b34fb")

class DeviceColorSaver @Inject constructor(
    private val dataStore: DataStore<PairSettings>
) {
    suspend fun saveDeviceColor(device: DiscoveredBluetoothDevice) {
        val deviceColor = when {
            device.services.contains(WHITE_UUID) -> HardwareColor.WHITE
            device.services.contains(BLACK_UUID) -> HardwareColor.BLACK
            device.services.contains(TRANSPARENT_UUID) -> HardwareColor.TRANSPARENT
            else -> null
        } ?: return
        dataStore.updateData {
            it.toBuilder()
                .setHardwareColor(deviceColor)
                .build()
        }
    }
}
