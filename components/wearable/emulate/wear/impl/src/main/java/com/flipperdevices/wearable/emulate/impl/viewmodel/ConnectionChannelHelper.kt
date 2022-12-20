package com.flipperdevices.wearable.emulate.impl.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.core.di.SingleIn
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.common.WearEmulateConstants
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.WearableCommandOutputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.impl.di.WearGraph
import com.flipperdevices.wearable.emulate.impl.model.ChannelState
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.ChannelClient.ChannelCallback
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface ConnectionChannelHelper {
    fun getState(): StateFlow<ChannelState>
    suspend fun establishConnection(nodeId: String)
    fun onClear()
}

@SingleIn(WearGraph::class)
@ContributesBinding(WearGraph::class, ConnectionChannelHelper::class)
class ConnectionChannelHelperImpl @Inject constructor(
    private val channelClient: ChannelClient,
    private val commandInputStream: WearableCommandInputStream<Main.MainResponse>,
    private val commandOutputStream: WearableCommandOutputStream<Main.MainRequest>,
    private val lifecycleOwner: LifecycleOwner
) : ChannelCallback(), ConnectionChannelHelper, LogTagProvider {
    override val TAG = "ConnectionChannelHelper"

    private val channelStateFlow = MutableStateFlow(ChannelState.DISCONNECTED)

    override fun getState() = channelStateFlow

    override suspend fun establishConnection(nodeId: String) {
        info { "#establishConnection" }
        val channel = channelClient.openChannel(
            nodeId,
            WearEmulateConstants.OPEN_CHANNEL_EMULATE
        ).await()
        info { "receive channel $channel" }
        channelClient.registerChannelCallback(channel, this)
        onChannelOpened(channel)
    }

    override fun onChannelOpened(channel: ChannelClient.Channel) {
        super.onChannelOpened(channel)
        info { "#onChannelOpened" }
        commandInputStream.onOpenChannel(lifecycleOwner.lifecycleScope, channel)
        commandOutputStream.onOpenChannel(lifecycleOwner.lifecycleScope, channel)
        info { "Notify that channel state is connected" }
        channelStateFlow.update { ChannelState.CONNECTED }
    }

    override fun onChannelClosed(
        channel: ChannelClient.Channel,
        closeReason: Int,
        appSpecificErrorCode: Int
    ) {
        super.onChannelClosed(channel, closeReason, appSpecificErrorCode)
        info { "#onChannelClosed $channel Reason: $closeReason AppCode: $appSpecificErrorCode" }
        commandInputStream.onCloseChannel(lifecycleOwner.lifecycleScope)
        commandOutputStream.onCloseChannel(lifecycleOwner.lifecycleScope)
        channelStateFlow.update { ChannelState.DISCONNECTED }
    }

    override fun onClear() {
        channelClient.unregisterChannelCallback(this)
    }
}
