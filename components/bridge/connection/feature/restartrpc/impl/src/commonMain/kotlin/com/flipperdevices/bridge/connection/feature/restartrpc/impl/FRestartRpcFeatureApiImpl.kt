package com.flipperdevices.bridge.connection.feature.restartrpc.impl

import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialRestartApi
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

class FRestartRpcFeatureApiImpl @AssistedInject constructor(
    @Assisted private val transportRestartApi: FSerialRestartApi
) : FRestartRpcFeatureApi {
    override suspend fun restartRpc() {
        transportRestartApi.restartRpc()
    }
    override suspend fun sendTrashBytesAndBrokeSession() {
        transportRestartApi.sendTrashBytesAndBrokeSession()
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            transportRestartApi: FSerialRestartApi
        ): FRestartRpcFeatureApiImpl
    }
}
