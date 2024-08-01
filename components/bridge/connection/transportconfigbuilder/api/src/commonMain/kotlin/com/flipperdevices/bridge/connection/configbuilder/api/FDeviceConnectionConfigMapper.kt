package com.flipperdevices.bridge.connection.configbuilder.api

import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import com.flipperdevices.bridge.connection.transport.common.api.FDeviceConnectionConfig

interface FDeviceConnectionConfigMapper {
    fun getConnectionConfig(device: FDeviceBaseModel): FDeviceConnectionConfig<*>
}
