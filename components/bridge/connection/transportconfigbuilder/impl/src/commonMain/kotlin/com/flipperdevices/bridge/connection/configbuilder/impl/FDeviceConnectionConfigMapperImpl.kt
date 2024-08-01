package com.flipperdevices.bridge.connection.configbuilder.impl

import com.flipperdevices.bridge.connection.config.api.model.FDeviceBaseModel
import com.flipperdevices.bridge.connection.config.api.model.FDeviceFlipperZeroBleModel
import com.flipperdevices.bridge.connection.configbuilder.api.FDeviceConnectionConfigMapper
import com.flipperdevices.bridge.connection.configbuilder.impl.builders.FlipperZeroBleBuilderConfig
import com.flipperdevices.bridge.connection.transport.common.api.FDeviceConnectionConfig
import com.flipperdevices.core.di.AppGraph
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FDeviceConnectionConfigMapper::class)
class FDeviceConnectionConfigMapperImpl @Inject constructor(
    private val flipperZeroBleBuilderConfig: FlipperZeroBleBuilderConfig
) : FDeviceConnectionConfigMapper {
    override fun getConnectionConfig(device: FDeviceBaseModel): FDeviceConnectionConfig<*> {
        return when (device) {
            is FDeviceFlipperZeroBleModel -> flipperZeroBleBuilderConfig.build(device.address)
        }
    }
}
