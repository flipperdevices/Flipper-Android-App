package com.flipperdevices.bridge.connection.config.api

import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import kotlinx.coroutines.flow.Flow

interface FDevicePersistedStorage {
    suspend fun getCurrentDevice(): Flow<FDeviceBaseModel?>
    suspend fun addDevice(device: FDeviceBaseModel)
    suspend fun removeDevice(id: String)
    fun getAllDevices(): Flow<List<FDeviceBaseModel>>
}