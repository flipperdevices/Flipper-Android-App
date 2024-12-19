package com.flipperdevices.keyemulate.helpers

import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperRequestPriority
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.app.appButtonReleaseRequest
import com.flipperdevices.protobuf.app.appExitRequest
import com.flipperdevices.protobuf.main
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface StopEmulateHelper {
    suspend fun onStop(
        requestApi: FlipperRequestApi,
        isPressRelease: Boolean = false
    )
}

@ContributesBinding(AppGraph::class, StopEmulateHelper::class)
class StopEmulateHelperImpl @Inject constructor() : StopEmulateHelper, LogTagProvider {
    override val TAG = "StopEmulateHelper"

    override suspend fun onStop(requestApi: FlipperRequestApi, isPressRelease: Boolean) {
        info { "stopEmulateInternal" }

        if (!isPressRelease) {
            val appButtonResponse = requestApi.request(
                flowOf(
                    main {
                        appButtonReleaseRequest = appButtonReleaseRequest { }
                    }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
                )
            )
            info { "App button stop response: $appButtonResponse" }
        }

        val appExitResponse = requestApi.request(
            flowOf(
                main {
                    appExitRequest = appExitRequest { }
                }.wrapToRequest(FlipperRequestPriority.FOREGROUND)
            )
        )
        info { "App exit response: $appExitResponse" }
    }
}
