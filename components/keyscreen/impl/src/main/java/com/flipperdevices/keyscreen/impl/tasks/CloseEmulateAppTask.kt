package com.flipperdevices.keyscreen.impl.tasks

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.keyscreen.impl.viewmodel.helpers.EmulateHelper
import kotlinx.coroutines.CoroutineScope

class CloseEmulateAppTask(
    serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper
) : OneTimeExecutionBleTask<Unit, Unit>(serviceProvider) {
    override val TAG = "CloseEmulateAppTask"

    override suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: Unit,
        stateListener: suspend (Unit) -> Unit
    ) {
        emulateHelper.stopEmulate(serviceApi.requestApi)
        stateListener(Unit)
    }

    override suspend fun onStopAsync(stateListener: suspend (Unit) -> Unit) {
        stateListener(Unit)
    }
}
