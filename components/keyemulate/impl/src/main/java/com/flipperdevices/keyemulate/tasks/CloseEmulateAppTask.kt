package com.flipperdevices.keyemulate.tasks

import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.EmulateHelper
import com.flipperdevices.core.ui.lifecycle.FOneTimeExecutionBleTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class CloseEmulateAppTask(
    private val emulateHelper: EmulateHelper
) : FOneTimeExecutionBleTask<Unit, Unit>() {
    override val TAG = "CloseEmulateAppTask"

    override suspend fun startInternal(
        scope: CoroutineScope,
        input: Unit,
        stateListener: suspend (Unit) -> Unit
    ) {
        emulateHelper.stopEmulateForce()
        // Waiting until the emulation stops
        emulateHelper.getCurrentEmulatingKey().filter { it == null }.first()
        stateListener(Unit)
    }

    override suspend fun onStopAsync(stateListener: suspend (Unit) -> Unit) {
        stateListener(Unit)
    }
}
