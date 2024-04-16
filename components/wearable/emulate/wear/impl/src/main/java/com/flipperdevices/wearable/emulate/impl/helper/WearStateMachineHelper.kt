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
import kotlinx.coroutines.flow.updateAndGet
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
                return lastState.updateAndGet { WearEmulateState.NotInitialized }
            }
            ChannelClientState.FINDING_NODE -> {
                return lastState.updateAndGet { WearEmulateState.NodeFinding }
            }
            ChannelClientState.NOT_FOUND_NODE -> {
                return lastState.updateAndGet { WearEmulateState.NotFoundNode }
            }
            ChannelClientState.CONNECTING -> {
                return lastState.updateAndGet { WearEmulateState.TestConnection }
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
                return lastState.updateAndGet { WearEmulateState.UnsupportedFlipper }
            }
            ConnectStatusOuterClass.ConnectStatus.READY -> {
                lastState.value = WearEmulateState.ReadyForEmulate(keyToEmulate.keyType)
            }
            else -> {
                return lastState.updateAndGet { WearEmulateState.ConnectingToFlipper }
            }
        }

        return when (emulateState) {
            Emulate.EmulateStatus.EMULATING -> {
                lastState.updateAndGet {
                    WearEmulateState.Emulating(keyToEmulate.keyType, EmulateProgress.Infinite)
                }
            }

            else -> lastState.value
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
