package com.flipperdevices.wearable.emulate.impl.helper

import androidx.compose.runtime.Stable
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.model.EmulateProgress
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.ConnectStatusOuterClass
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.Emulate
import com.flipperdevices.wearable.emulate.impl.viewmodel.KeyToEmulate
import com.flipperdevices.wearable.emulate.model.ChannelClientState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface WearStateMachineHelper {
    fun combineStates(
        channelState: ChannelClientState,
        connectionState: ConnectionTesterState,
        flipperState: ConnectStatusOuterClass.ConnectStatus,
        emulateState: Emulate.EmulateStatus,
        keyToEmulate: KeyToEmulate
    ): WearEmulateState
}

@Singleton
@ContributesBinding(AppGraph::class, WearStateMachineHelper::class)
class WearStateMachineHelperImpl @Inject constructor(
    private val flipperStatusHelper: FlipperStatusHelper,
    private val connectionHelper: ConnectionHelper
) : WearStateMachineHelper, LogTagProvider {

    override val TAG: String = "WearStateMachineHelper"

    private val lastState = MutableStateFlow<WearEmulateState>(WearEmulateState.NotInitialized)

    override fun combineStates(
        channelState: ChannelClientState,
        connectionState: ConnectionTesterState,
        flipperState: ConnectStatusOuterClass.ConnectStatus,
        emulateState: Emulate.EmulateStatus,
        keyToEmulate: KeyToEmulate
    ): WearEmulateState {
        info { "#processStates $channelState $connectionState $flipperState $emulateState" }

        when (channelState) {
            ChannelClientState.DISCONNECTED -> {
                lastState.value = WearEmulateState.NotInitialized
                return lastState.value
            }
            ChannelClientState.FINDING_NODE -> {
                lastState.value = WearEmulateState.NodeFinding
                return lastState.value
            }
            ChannelClientState.NOT_FOUND_NODE -> {
                lastState.value = WearEmulateState.NotFoundNode
                return lastState.value
            }
            ChannelClientState.CONNECTING -> {
                lastState.value = WearEmulateState.TestConnection
                return lastState.value
            }
            ChannelClientState.OPENED -> {}
        }

        when (connectionState) {
            ConnectionTesterState.NOT_CONNECTED -> {
                lastState.value = WearEmulateState.TestConnection
                connectionHelper.testConnection()
                return lastState.value
            }
            ConnectionTesterState.CONNECTED -> flipperStatusHelper.onSubscribe()
        }

        when (flipperState) {
            ConnectStatusOuterClass.ConnectStatus.UNSUPPORTED -> {
                lastState.value = WearEmulateState.UnsupportedFlipper
                return lastState.value
            }
            ConnectStatusOuterClass.ConnectStatus.READY ->
                lastState.value = WearEmulateState.ReadyForEmulate(keyToEmulate.keyType)
            else -> {
                lastState.value = WearEmulateState.ConnectingToFlipper
                return lastState.value
            }
        }

        when (emulateState) {
            Emulate.EmulateStatus.EMULATING -> {
                lastState.value = WearEmulateState.Emulating(keyToEmulate.keyType, EmulateProgress.Infinite)
                return lastState.value
            }
            else -> return lastState.value
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
