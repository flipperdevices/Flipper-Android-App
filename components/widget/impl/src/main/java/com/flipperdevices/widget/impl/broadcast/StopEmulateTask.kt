package com.flipperdevices.widget.impl.broadcast

import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.keyscreen.api.EmulateHelper
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.InvalidateWidgetsTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class StopEmulateTask(
    serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper,
    private val invalidateWidgetsTask: InvalidateWidgetsTask,
    private val widgetStateStorage: WidgetStateStorage
) : OneTimeExecutionBleTask<Int, Unit>(serviceProvider) {
    override val TAG = "StartEmulateTask"

    override suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: Int,
        stateListener: suspend (Unit) -> Unit
    ) {
        emulateHelper.stopEmulate(scope, serviceApi.requestApi)
        emulateHelper.getCurrentEmulatingKey().filter { it == null }.first()
        widgetStateStorage.updateState(input, WidgetState.PENDING)
        invalidateWidgetsTask.invoke()
    }

    override suspend fun onStopAsync(stateListener: suspend (Unit) -> Unit) {
        stateListener(Unit)
    }
}