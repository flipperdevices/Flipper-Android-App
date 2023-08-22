package com.flipperdevices.wearable.emulate.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.wearable.emulate.api.ChannelClientHelper
import com.flipperdevices.wearable.emulate.api.HandheldProcessor
import com.flipperdevices.wearable.emulate.common.WearEmulateConstants
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.impl.helper.NodeFindingHelper
import com.google.android.gms.wearable.ChannelClient
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, ChannelClientHelper::class)
class ChannelClientHelperImpl @Inject constructor(
    private val channelClient: ChannelClient,
    private val nodeFindingHelper: NodeFindingHelper,
    private val commandInputStream: WearableCommandInputStream<Main.MainResponse>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainRequest>,
    private val handheldProcessors: MutableSet<HandheldProcessor>
) : ChannelClientHelper, LogTagProvider {
    override val TAG: String = "ChannelClientHelper"

    private var activeChannel: ChannelClient.Channel? = null
    private val state = MutableStateFlow(ChannelClientState.DISCONNECTED)

    override fun onChannelOpen(scope: CoroutineScope) {
        info { "#onChannelOpen" }
        scope.launch(Dispatchers.Default) {
            onChannelOpenInvalidate(scope)
        }
    }

    override fun onChannelReset(scope: CoroutineScope) {
        info { "#onChannelReset" }
        scope.launch(Dispatchers.Default) {
            resetState(scope)
            onChannelOpenInvalidate(scope)
        }
    }

    private suspend fun onChannelOpenInvalidate(scope: CoroutineScope) {
        info { "#onChannelOpenInvalidate" }
        state.emit(ChannelClientState.FINDING_NODE)
        val nodeId = nodeFindingHelper.findNode()
        if (nodeId == null) {
            state.emit(ChannelClientState.NOT_FOUND_NODE)
            return
        }
        state.emit(ChannelClientState.CONNECTING)
        val channel = runCatching {
            channelClient.openChannel(
                nodeId,
                WearEmulateConstants.OPEN_CHANNEL_EMULATE
            ).await()
        }.getOrNull()

        if (channel == null) {
            warn { "Channel was null" }
            state.emit(ChannelClientState.NOT_FOUND_NODE)
            return
        }

        activeChannel = channel
        commandInputStream.onOpenChannel(scope, channel)
        commandOutputStream.onOpenChannel(scope, channel)
        state.emit(ChannelClientState.OPENED)

        handheldProcessors.forEach { it.init(scope) }
    }

    override fun onCloseChannel(scope: CoroutineScope) {
        info { "#onCloseChannel" }

        scope.launch(Dispatchers.Default) {
            val currentChannel = activeChannel
            if (currentChannel == null) {
                warn { "Active channel was null" }
                return@launch
            }

            resetState(scope)

            runCatching {
                channelClient.close(currentChannel).await()
            }.onFailure {
                warn { "Failed to close channel" }
            }.onSuccess {
                info { "Channel closed" }
            }

            activeChannel = null
        }
    }

    private suspend fun resetState(scope: CoroutineScope) {
        info { "#resetState" }
        state.emit(ChannelClientState.DISCONNECTED)
        commandInputStream.onCloseChannel(scope)
        commandOutputStream.onCloseChannel(scope)
        handheldProcessors.forEach { it.reset() }
    }
}

enum class ChannelClientState {
    DISCONNECTED,
    FINDING_NODE,
    NOT_FOUND_NODE,
    CONNECTING,
    OPENED
}
