package com.flipperdevices.bridge.connection.configbuilder.api

import com.flipperdevices.bridge.connection.transport.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel

interface FDeviceConnectionConfigMapper {
    fun getConnectionConfig(device: FDeviceBaseModel): FDeviceConnectionConfig<*>
}