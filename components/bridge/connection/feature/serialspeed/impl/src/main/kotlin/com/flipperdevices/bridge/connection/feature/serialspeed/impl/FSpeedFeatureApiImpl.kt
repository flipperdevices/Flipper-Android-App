package com.flipperdevices.bridge.connection.feature.serialspeed.impl

import com.flipperdevices.bridge.connection.feature.serialspeed.api.FSpeedFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FlipperSerialSpeed
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.StateFlow

class FSpeedFeatureApiImpl @AssistedInject constructor(
    @Assisted private val serialApi: FSerialDeviceApi
) : FSpeedFeatureApi {
    override suspend fun getSpeed(): StateFlow<FlipperSerialSpeed> {
        return serialApi.getSpeed()
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            serialApi: FSerialDeviceApi
        ): FSpeedFeatureApiImpl
    }
}
