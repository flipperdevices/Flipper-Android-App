package com.flipperdevices.bridge.connection.feature.restartrpc.impl

import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialRestartApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class FRestartRpcFeatureApiImpl @AssistedInject constructor(
    @Assisted private val transportRestartApi: FSerialRestartApi
) : FRestartRpcFeatureApi {
    override suspend fun restartRpc() {
        transportRestartApi.restartRpc()
    }

    @AssistedFactory
    fun interface InternalFactory {
        operator fun invoke(
            transportRestartApi: FSerialRestartApi
        ): FRestartRpcFeatureApiImpl
    }
}
