package com.flipperdevices.wearable.emulate.handheld.impl.request

import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.protobuf.app.AppState
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.Emulate
import com.flipperdevices.wearable.emulate.handheld.impl.di.WearHandheldGraph
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@SingleIn(WearHandheldGraph::class)
@ContributesMultibinding(WearHandheldGraph::class, WearableCommandProcessor::class)
class WearableAppStateProcessor @Inject constructor(
    private val commandOutputStream: WearableCommandOutputStream<Main.MainResponse>,
    private val scope: CoroutineScope,
    private val fFeatureProvider: FFeatureProvider
) : WearableCommandProcessor, LogTagProvider {
    override val TAG: String = "WearableAppStateProcessor-${hashCode()}"
    override fun init() {
        scope.launch {
            fFeatureProvider.get<FEmulateFeatureApi>()
                .map { status -> status as? FFeatureStatus.Supported<FEmulateFeatureApi> }
                .map { status -> status?.featureApi }
                .flatMapLatest { feature ->
                    feature?.getAppEmulateHelper()?.appStateFlow() ?: flowOf(null)
                }.filterNotNull().collect { appStateResponse ->
                    info { "AppState: $appStateResponse" }
                    if (appStateResponse.state == AppState.APP_CLOSED) {
                        commandOutputStream.send(
                            mainResponse {
                                emulateStatus = Emulate.EmulateStatus.STOPPED
                            }
                        )
                    }
                }
        }
    }
}
