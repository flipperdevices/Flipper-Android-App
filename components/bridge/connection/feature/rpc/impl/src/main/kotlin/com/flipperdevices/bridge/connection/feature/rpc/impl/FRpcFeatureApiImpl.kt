package com.flipperdevices.bridge.connection.feature.rpc.impl

import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.di.AppGraph
import dagger.assisted.Assisted
import kotlinx.coroutines.CoroutineScope
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, FRpcFeatureApi.Factory::class)
class FRpcFeatureApiImpl constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val serialApi: FSerialDeviceApi
) : FRpcFeatureApi
