package com.flipperdevices.bridge.connection.config.impl

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.connection.config.api.FDevicePersistedStorage
import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroBleModel
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.core.preference.pb.NewPairSettings
import com.flipperdevices.core.preference.pb.SavedDevice
import com.flipperdevices.core.preference.pb.SavedDevice.DataCase
import com.flipperdevices.core.preference.pb.flipperZeroBle
import com.flipperdevices.core.preference.pb.savedDevice
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
            val deviceId = settings.currentSelectedDeviceId
            if (deviceId.isNullOrBlank()) {
                return@map null
            } else {
                settings.devicesList.find { it.id == deviceId }
                    ?.run(::mapSavedDevice)
            }
        }
    }

    override suspend fun setCurrentDevice(id: String?) {
        newPairSettings.updateData { settings ->
            var builder = settings.toBuilder()
            if (id == null) {
                builder = builder
                    .clearCurrentSelectedDeviceId()
            } else if (settings.devicesList.find { it.id == id } == null) {
                error("Can't find device with id $id")
            } else {
                builder = builder
                    .setCurrentSelectedDeviceId(id)
            }
            builder.build()
        }
    }

    override suspend fun addDevice(device: FDeviceBaseModel) {
        info { "Add device $device" }
        newPairSettings.updateData { settings ->
            settings.toBuilder()
                .addDevices(mapDeviceBaseModelToSavedDevice(device))
                .build()
        }
    }

    override suspend fun removeDevice(id: String) {
        newPairSettings.updateData { settings ->
            val deviceIndex = settings.devicesList.indexOfFirst { it.id == id }
            if (deviceIndex < 0) {
                warn { "Can't find device with id $id" }
                settings
            } else {
                settings.toBuilder()
                    .removeDevices(deviceIndex)
                    .build()
            }
        }
    }

    override fun getAllDevices(): Flow<List<FDeviceBaseModel>> {
        return newPairSettings.data.map { settings ->
            settings.devicesList.mapNotNull { device ->
                mapSavedDevice(device)
            }
        }
    }

    private fun mapSavedDevice(device: SavedDevice): FDeviceBaseModel? {
        return when (device.dataCase) {
            DataCase.FLIPPER_ZERO_BLE -> {
                FDeviceFlipperZeroBleModel(
                    name = device.name,
                    uniqueId = device.id,
                    address = device.flipperZeroBle.address
                )
            }

            DataCase.DATA_NOT_SET -> null
        }
    }

    private fun mapDeviceBaseModelToSavedDevice(device: FDeviceBaseModel): SavedDevice {
        return when (device) {
            is FDeviceFlipperZeroBleModel -> savedDevice {
                id = device.uniqueId
                name = device.name
                flipperZeroBle = flipperZeroBle {
                    address = device.address
                }
            }

            else -> error("Can't find parser for $device")
        }
    }
}
