package com.flipperdevices.wearable.emulate.impl.helper

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
import com.flipperdevices.wearable.emulate.model.ChannelClientState
import com.google.android.gms.wearable.ChannelClient
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, ChannelClientHelper::class)
class ChannelClientHelper @Inject constructor(
    private val channelClient: ChannelClient,
    private val nodeFindingHelper: NodeFindingHelper,
    private val commandInputStream: WearableCommandInputStream<Main.MainResponse>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainRequest>,
    private val handheldProcessors: MutableSet<HandheldProcessor>
) : ChannelClientHelper, LogTagProvider {
    override val TAG: String = "ChannelClientHelper"

    private val state = MutableStateFlow(ChannelClientState.DISCONNECTED)
    override fun getState() = state.asStateFlow()

    override suspend fun onChannelOpen(scope: CoroutineScope): ChannelClient.Channel? {
        info { "#onChannelOpen" }
        state.emit(ChannelClientState.FINDING_NODE)
        val nodeId = nodeFindingHelper.findNode()
        if (nodeId == null) {
            state.emit(ChannelClientState.NOT_FOUND_NODE)
            return null
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
            return null
        }

        commandInputStream.onOpenChannel(scope, channel)
        commandOutputStream.onOpenChannel(scope, channel)
        state.emit(ChannelClientState.OPENED)

        handheldProcessors.forEach { it.init(scope) }

        return channel
    }

    override suspend fun onChannelReset(scope: CoroutineScope): ChannelClient.Channel? {
        handheldProcessors.forEach { it.reset(scope) }
        commandInputStream.onCloseChannel(scope)
        commandOutputStream.onCloseChannel(scope)
        state.emit(ChannelClientState.DISCONNECTED)
        return onChannelOpen(scope)
    }
}
