package com.flipperdevices.keyscreen.emulate.tasks

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.keyscreen.emulate.viewmodel.helpers.EmulateHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

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
        // Waiting until the emulation stops
        emulateHelper.getRunningState().filter { !it }.first()
        stateListener(Unit)
    }

    override suspend fun onStopAsync(stateListener: suspend (Unit) -> Unit) {
        stateListener(Unit)
    }
}
