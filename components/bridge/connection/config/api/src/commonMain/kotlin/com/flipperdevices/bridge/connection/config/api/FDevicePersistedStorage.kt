package com.flipperdevices.bridge.connection.config.api

import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import com.flipperdevices.core.preference.pb.SavedDevice
import kotlinx.coroutines.flow.Flow

interface FDevicePersistedStorage {
    fun getCurrentDevice(): Flow<FDeviceBaseModel?>
    suspend fun setCurrentDevice(id: String?)
    suspend fun addDevice(device: FDeviceBaseModel)
    suspend fun removeDevice(id: String)
    fun getAllDevices(): Flow<List<FDeviceBaseModel>>
    suspend fun updateDevice(id: String, block: (SavedDevice) -> SavedDevice)
}
