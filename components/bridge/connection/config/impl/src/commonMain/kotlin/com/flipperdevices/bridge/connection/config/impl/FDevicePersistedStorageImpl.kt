package com.flipperdevices.bridge.connection.config.impl

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroBleModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.preference.pb.FlipperZeroBle
import com.flipperdevices.core.preference.pb.NewPairSettings
import com.flipperdevices.core.preference.pb.SavedDevice
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FDevicePersistedStorage::class)
class FDevicePersistedStorageImpl @Inject constructor(
    private val newPairSettings: DataStore<NewPairSettings>
) : FDevicePersistedStorage, LogTagProvider {
    override val TAG = "FDevicePersistedStorage"

    override fun getCurrentDevice(): Flow<FDeviceBaseModel?> {
        return newPairSettings.data.map { settings ->
            val deviceId = settings.current_selected_device_id
            if (deviceId.isBlank()) {
                return@map null
            } else {
                settings.devices.find { it.id == deviceId }
                    ?.run(::mapSavedDevice)
            }
        }
    }

    override suspend fun setCurrentDevice(id: String?) {
        newPairSettings.updateData { settings ->
            if (id == null) {
                settings.copy(current_selected_device_id = "")
            } else if (settings.devices.none { it.id == id }) {
                error("Can't find device with id $id")
            } else {
                settings.copy(current_selected_device_id = id)
            }
        }
    }

    override suspend fun addDevice(device: FDeviceBaseModel) {
        info { "Add device $device" }
        newPairSettings.updateData { settings ->
            settings.copy(
                devices = settings.devices.plus(mapDeviceBaseModelToSavedDevice(device))
            )
        }
    }

    override suspend fun removeDevice(id: String) {
        newPairSettings.updateData { settings ->
            val devicesList = settings.devices.toMutableList()
            val deviceIndex = devicesList.indexOfFirst { it.id == id }
            if (deviceIndex < 0) {
                warn { "Can't find device with id $id" }
                settings
            } else {
                devicesList.removeAt(deviceIndex)
                settings.copy(
                    devices = devicesList
                )
            }
        }
    }

    override fun getAllDevices(): Flow<List<FDeviceBaseModel>> {
        return newPairSettings.data.map { settings ->
            settings.devices.mapNotNull { device ->
                mapSavedDevice(device)
            }
        }
    }

    private fun mapSavedDevice(device: SavedDevice): FDeviceBaseModel? {
        val flipperZeroBle = device.flipper_zero_ble
        if (flipperZeroBle != null) {
            return FDeviceFlipperZeroBleModel(
                name = device.name,
                uniqueId = device.id,
                address = flipperZeroBle.address
            )
        }
        return null
    }

    private fun mapDeviceBaseModelToSavedDevice(device: FDeviceBaseModel): SavedDevice {
        return when (device) {
            is FDeviceFlipperZeroBleModel -> SavedDevice(
                id = device.uniqueId,
                name = device.name,
                flipper_zero_ble = FlipperZeroBle(
                    address = device.address
                )
            )

            else -> error("Can't find parser for $device")
        }
    }
}
