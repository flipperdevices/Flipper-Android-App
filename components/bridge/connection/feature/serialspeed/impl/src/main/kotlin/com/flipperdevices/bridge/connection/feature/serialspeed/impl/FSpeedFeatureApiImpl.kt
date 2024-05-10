package com.flipperdevices.bridge.connection.feature.serialspeed.impl

import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FlipperSerialSpeed
import com.flipperdevices.core.di.AppGraph
import dagger.assisted.Assisted
import kotlinx.coroutines.flow.StateFlow
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FSpeedFeatureApi.Factory::class)
class FSpeedFeatureApiImpl constructor(
    @Assisted private val serialApi: FSerialDeviceApi
) : FSpeedFeatureApi {
    override suspend fun getSpeed(): StateFlow<FlipperSerialSpeed> {
        return serialApi.getSpeed()
    }
}
