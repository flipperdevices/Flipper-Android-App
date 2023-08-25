package com.flipperdevices.wearable.emulate.impl.helper

import androidx.compose.runtime.Stable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.ConnectStatusOuterClass
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.Emulate
import com.flipperdevices.wearable.emulate.impl.model.KeyToEmulate
import com.flipperdevices.wearable.emulate.model.ChannelClientState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface WearStateMachineHelper {
    fun getState(): StateFlow<WearEmulateState>

    suspend fun onStatesUpdated(
        channelState: ChannelClientState,
        connectionState: ConnectionTesterState,
        flipperState: ConnectStatusOuterClass.ConnectStatus,
        emulateState: Emulate.EmulateStatus,
        keyToEmulate: KeyToEmulate
    )
}

@Singleton
@ContributesBinding(AppGraph::class, WearStateMachineHelper::class)
class WearStateMachineHelperImpl @Inject constructor(
    private val flipperStatusHelper: FlipperStatusHelper,
    private val emulateHelper: EmulateHelper
) : WearStateMachineHelper, LogTagProvider {

    override val TAG: String = "WearStateMachineHelper"

    private val state = MutableStateFlow<WearEmulateState>(WearEmulateState.NotInitialized)
    override fun getState() = state.asStateFlow()

    override suspend fun onStatesUpdated(
        channelState: ChannelClientState,
        connectionState: ConnectionTesterState,
        flipperState: ConnectStatusOuterClass.ConnectStatus,
        emulateState: Emulate.EmulateStatus,
        keyToEmulate: KeyToEmulate
    ) {
        info { "#processStates $channelState $connectionState $flipperState $emulateState" }

        when (channelState) {
            ChannelClientState.DISCONNECTED -> {
                state.emit(WearEmulateState.NotInitialized)
                return
            }
            ChannelClientState.FINDING_NODE -> {
                state.emit(WearEmulateState.NodeFinding)
                return
            }
            ChannelClientState.NOT_FOUND_NODE -> {
                state.emit(WearEmulateState.NotFoundNode)
                return
            }
            ChannelClientState.CONNECTING -> {
                state.emit(WearEmulateState.TestConnection)
                return
            }
            ChannelClientState.OPENED -> {}
        }

        when (connectionState) {
            ConnectionTesterState.NOT_CONNECTED -> {
                state.emit(WearEmulateState.TestConnection)
                return
            }
            ConnectionTesterState.CONNECTED -> {
                flipperStatusHelper.onSubscribe()
            }
        }

        when (flipperState) {
            ConnectStatusOuterClass.ConnectStatus.UNSUPPORTED -> {
                state.emit(WearEmulateState.UnsupportedFlipper)
                return
            }
            ConnectStatusOuterClass.ConnectStatus.READY -> {
                state.emit(WearEmulateState.ReadyForEmulate(keyToEmulate.keyType))
            }
            else -> {
                state.emit(WearEmulateState.ConnectingToFlipper)
                return
            }
        }

        when (emulateState) {
            Emulate.EmulateStatus.EMULATING -> {
                state.emit(WearEmulateState.Emulating(keyToEmulate.keyType, EmulateProgress.Infinite))
                return
            }
            else -> {
                return
            }
        }
    }
}

@Stable
sealed class WearEmulateState {
    open val keyType: FlipperKeyType? = null

    data object NotInitialized : WearEmulateState()

    data object NodeFinding : WearEmulateState()

    data object NotFoundNode : WearEmulateState()

    data object TestConnection : WearEmulateState()

    data object ConnectingToFlipper : WearEmulateState()

    data object UnsupportedFlipper : WearEmulateState()

    @Stable
    data class ReadyForEmulate(
        override val keyType: FlipperKeyType?
    ) : WearEmulateState()

    @Stable
    data class Emulating(
        override val keyType: FlipperKeyType?,
        val progress: EmulateProgress
    ) : WearEmulateState()
}
