package com.flipperdevices.widget.impl.broadcast

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.keyscreen.api.EmulateHelper
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.InvalidateWidgetsTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StartEmulateTask(
    serviceProvider: FlipperServiceProvider,
    private val emulateHelper: EmulateHelper,
    private val widgetStateStorage: WidgetStateStorage,
    private val invalidateWidgetsTask: InvalidateWidgetsTask
) : OneTimeExecutionBleTask<Pair<FlipperKeyPath, Int>, Unit>(serviceProvider) {
    override val TAG = "StartEmulateTask"

    override suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: Pair<FlipperKeyPath, Int>,
        stateListener: suspend (Unit) -> Unit
    ) {
        val keyType = input.first.path.keyType ?: return
        emulateHelper.getCurrentEmulatingKey().onEach {
            invalidateWidgetsTask.invoke()
        }.launchIn(scope)
        try {
            emulateHelper.startEmulate(scope, serviceApi.requestApi, keyType, input.first.path)
        } catch (throwable: Throwable) {
            error(throwable) { "Failed emulate $input" }
            widgetStateStorage.updateState(input.second, WidgetState.ERROR)
            invalidateWidgetsTask.invoke()
        }
    }

    override suspend fun onStopAsync(stateListener: suspend (Unit) -> Unit) {
        stateListener(Unit)
    }
}
