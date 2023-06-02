package com.flipperdevices.keyemulate.tasks

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.keyemulate.api.EmulateHelper
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
        emulateHelper.stopEmulateForce(serviceApi.requestApi)
        // Waiting until the emulation stops
        emulateHelper.getCurrentEmulatingKey().filter { it == null }.first()
        stateListener(Unit)
    }

    override suspend fun onStopAsync(stateListener: suspend (Unit) -> Unit) {
        stateListener(Unit)
    }
}
