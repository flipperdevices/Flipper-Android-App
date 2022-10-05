package com.flipperdevices.widget.impl.broadcast

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.keyscreen.api.EmulateHelper
import kotlinx.coroutines.CoroutineScope

class StartEmulateTask(
    serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper
) : OneTimeExecutionBleTask<FlipperKeyPath, Unit>(serviceProvider) {
    override val TAG = "StartEmulateTask"

    override suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: FlipperKeyPath,
        stateListener: suspend (Unit) -> Unit
    ) {
        val keyType = input.path.keyType ?: return
        emulateHelper.startEmulate(scope, serviceApi.requestApi, keyType, input.path)
    }

    override suspend fun onStopAsync(stateListener: suspend (Unit) -> Unit) {
        stateListener(Unit)
    }
}