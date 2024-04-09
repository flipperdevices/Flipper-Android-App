package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.common.api.FConnectedDeviceApi
import com.flipperdevices.bridge.connection.common.api.FDeviceConnectionConfig
import com.flipperdevices.bridge.connection.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.connectionbuilder.api.FDeviceConfigToConnection
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import javax.inject.Inject

class FDeviceHolder<API : FConnectedDeviceApi> @AssistedInject constructor(
    @Assisted private val config: FDeviceConnectionConfig<API>,
    @Assisted private val listener: FTransportConnectionStatusListener,
    @Assisted private val onConnectError: (Throwable) -> Unit,
    private val deviceConnectionHelper: FDeviceConfigToConnection
) : LogTagProvider {
    override val TAG = "FDeviceHolder-$config"

    private val scope = CoroutineScope(Dispatchers.Default)
    private var deviceApi: API? = null
    private val connectJob: Job = scope.launch {
        deviceApi = deviceConnectionHelper.connect(
            scope, config, listener
        ).onFailure(onConnectError).getOrNull()
    }

    suspend fun disconnect() {
        info { "Cancel connect job" }
        connectJob.cancelAndJoin()
        if (deviceApi != null) {
            info { "Find active device api, start disconnect" }
            deviceApi?.disconnect()
        }
        info { "Cancel scope" }
        scope.cancel()
    }

    @AssistedFactory
    interface Factory {
        fun <API : FConnectedDeviceApi> build(
            @Assisted config: FDeviceConnectionConfig<API>,
            @Assisted listener: FTransportConnectionStatusListener,
            @Assisted onConnectError: (Throwable) -> Unit
        ): FDeviceHolder<API>
    }
}