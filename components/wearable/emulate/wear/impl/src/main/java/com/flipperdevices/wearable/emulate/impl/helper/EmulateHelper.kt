package com.flipperdevices.wearable.emulate.impl.helper

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.api.HandheldProcessor
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.mainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.Emulate
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.sendRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.startEmulateRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.stopEmulateRequest
import com.flipperdevices.wearable.emulate.impl.viewmodel.KeyToEmulate
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface EmulateHelper {

    fun onClickEmulate(keyToEmulate: KeyToEmulate)

    fun onShortEmulate(keyToEmulate: KeyToEmulate)

    fun onStopEmulate()

    fun getState(): StateFlow<Emulate.EmulateStatus>
}

@Singleton
@ContributesBinding(AppGraph::class, EmulateHelper::class)
@ContributesMultibinding(AppGraph::class, HandheldProcessor::class)
class EmulateHelperImpl @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainResponse>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainRequest>,
) : EmulateHelper, HandheldProcessor, LogTagProvider {
    override val TAG: String = "EmulateHelper-${hashCode()}"

    private val state = MutableStateFlow(Emulate.EmulateStatus.UNRECOGNIZED)

    override fun getState() = state.asStateFlow()

    override fun init(scope: CoroutineScope) {
        commandInputStream.getRequestsFlow().onEach {
            if (it.hasEmulateStatus()) {
                info { "#hasEmulateStatus $it" }
                state.emit(it.emulateStatus)
            }
        }.launchIn(scope)
    }

    override fun reset(scope: CoroutineScope) {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            state.emit(Emulate.EmulateStatus.UNRECOGNIZED)
        }
    }

    override fun onClickEmulate(keyToEmulate: KeyToEmulate) {
        commandOutputStream.send(
            mainRequest {
                startEmulate = startEmulateRequest {
                    path = keyToEmulate.keyPath
                }
            }
        )
    }

    override fun onShortEmulate(keyToEmulate: KeyToEmulate) {
        commandOutputStream.send(
            mainRequest {
                sendRequest = sendRequest {
                    path = keyToEmulate.keyPath
                }
            }
        )
    }

    override fun onStopEmulate() {
        commandOutputStream.send(
            mainRequest {
                stopEmulate = stopEmulateRequest { }
            }
        )
    }
}
