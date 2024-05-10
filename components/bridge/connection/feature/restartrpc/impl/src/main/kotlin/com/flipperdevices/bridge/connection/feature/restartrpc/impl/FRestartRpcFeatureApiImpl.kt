package com.flipperdevices.bridge.connection.feature.restartrpc.impl

import com.flipperdevices.bridge.connection.feature.restartrpc.api.FRestartRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialRestartApi
import com.flipperdevices.core.di.AppGraph
import dagger.assisted.Assisted
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FRestartRpcFeatureApi.Factory::class)
class FRestartRpcFeatureApiImpl constructor(
    @Assisted private val transportRestartApi: FSerialRestartApi
) : FRestartRpcFeatureApi {
    override suspend fun restartRpc() {
        transportRestartApi.restartRpc()
    }
}
