package com.flipperdevices.keyscreen.impl.tasks

import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.ui.lifecycle.OneTimeExecutionBleTask
import com.flipperdevices.protobuf.app.appButtonReleaseRequest
import com.flipperdevices.protobuf.app.appExitRequest
import com.flipperdevices.protobuf.main
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf

class CloseEmulateAppTask(
    serviceProvider: FlipperServiceProvider
) : OneTimeExecutionBleTask<Unit, Unit>(serviceProvider) {
    override val TAG = "CloseEmulateAppTask"

    override suspend fun startInternal(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        input: Unit,
        stateListener: suspend (Unit) -> Unit
    ) {
        serviceApi.requestApi.request(
            flowOf(
                main {
                    appButtonReleaseRequest = appButtonReleaseRequest { }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        serviceApi.requestApi.request(
            flowOf(
                main {
                    appExitRequest = appExitRequest { }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        stateListener(Unit)
    }

    override suspend fun onStopAsync(stateListener: suspend (Unit) -> Unit) {
        stateListener(Unit)
    }
}
