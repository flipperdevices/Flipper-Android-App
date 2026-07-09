package com.flipperdevices.wearable.emulate.impl.helper

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.api.HandheldProcessor
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.MainResponse
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.EmulateStatus
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.SendRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.StartEmulateRequest
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.StopEmulateRequest
import com.flipperdevices.wearable.emulate.impl.viewmodel.KeyToEmulate
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.ContributesIntoSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding

interface EmulateHelper {

    fun onClickEmulate(keyToEmulate: KeyToEmulate)

    fun onShortEmulate(keyToEmulate: KeyToEmulate)

    fun onStopEmulate()

    fun getState(): StateFlow<EmulateStatus>
}

@SingleIn(AppGraph::class)
@ContributesBinding(AppGraph::class, binding<EmulateHelper>())
@ContributesIntoSet(AppGraph::class, binding<HandheldProcessor>())
class EmulateHelperImpl @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<MainResponse>,
    private val commandOutputStream: WearableCommandOutputStream<MainRequest>,
) : EmulateHelper, HandheldProcessor, LogTagProvider {
    override val TAG: String = "EmulateHelper-${hashCode()}"

    private val state = MutableStateFlow<EmulateStatus>(EmulateStatus.fromValue(-1))

    override fun getState() = state.asStateFlow()

    override fun init(scope: CoroutineScope) {
        commandInputStream.getRequestsFlow().onEach {
            val emulateStatus = it.emulate_status
            if (emulateStatus != null) {
                info { "#hasEmulateStatus $it" }
                state.emit(emulateStatus)
            }
        }.launchIn(scope)
    }

    override fun reset(scope: CoroutineScope) {
        scope.launch(FlipperDispatchers.workStealingDispatcher) {
            state.emit(EmulateStatus.fromValue(-1))
        }
    }

    override fun onClickEmulate(keyToEmulate: KeyToEmulate) {
        commandOutputStream.send(
            MainRequest(start_emulate = StartEmulateRequest(path = keyToEmulate.keyPath))
        )
    }

    override fun onShortEmulate(keyToEmulate: KeyToEmulate) {
        commandOutputStream.send(
            MainRequest(send_request = SendRequest(path = keyToEmulate.keyPath))
        )
    }

    override fun onStopEmulate() {
        commandOutputStream.send(
            MainRequest(stop_emulate = StopEmulateRequest())
        )
    }
}
