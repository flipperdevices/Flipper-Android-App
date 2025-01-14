package com.flipperdevices.bridge.connection.feature.emulate.impl.api.helpers

import com.flipperdevices.bridge.connection.feature.emulate.api.helpers.StopEmulateHelper
import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.api.FRpcFeatureApi
import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.rpc.model.wrapToRequest
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.Main
import com.flipperdevices.protobuf.app.AppButtonReleaseRequest
import com.flipperdevices.protobuf.app.AppExitRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn

class StopEmulateHelperImpl(
    private val fRpcFeatureApi: FRpcFeatureApi,
    private val fVersionFeatureApi: FVersionFeatureApi,
    private val scope: CoroutineScope
) : StopEmulateHelper, LogTagProvider {
    override val TAG = "StopEmulateHelper"

    private val isPressReleaseSupported = flow {
        emit(fVersionFeatureApi.isSupported(API_SUPPORTED_INFRARED_PRESS_RELEASE))
    }.shareIn(scope, SharingStarted.Lazily, 1)

    override suspend fun onStop(isPressRelease: Boolean) {
        info { "stopEmulateInternal" }

        if (!isPressRelease || !isPressReleaseSupported.first()) {
            val appButtonResponse = fRpcFeatureApi.requestOnce(
                Main(
                    app_button_release_request = AppButtonReleaseRequest()
                ).wrapToRequest(FlipperRequestPriority.FOREGROUND)
            ).onFailure { error(it) { "#onStop could not release button" } }.getOrNull()
            info { "App button stop response: $appButtonResponse" }
        }

        val appExitResponse = fRpcFeatureApi.requestOnce(
            Main(
                app_exit_request = AppExitRequest()
            ).wrapToRequest(FlipperRequestPriority.FOREGROUND)
        ).onFailure { error(it) { "#onStop could not exit app" } }.getOrNull()
        info { "App exit response: $appExitResponse" }
    }
}
