package com.flipperdevices.wearable.emulate.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.wearable.emulate.api.ChannelClientHelper
import com.google.android.gms.wearable.ChannelClient
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, ChannelClientHelper::class)
class ChannelClientHelperImpl @Inject constructor(
    private val channelClient: ChannelClient,
) : ChannelClientHelper, LogTagProvider {
    override val TAG: String = "ChannelClientHelper"

    private var activeChannel: ChannelClient.Channel? = null

    override fun onChannelOpen(scope: CoroutineScope) {
        info { "#onChannelOpen" }
    }
    override fun onChannelCloseFromPhone(scope: CoroutineScope) {
        info { "#onChannelCloseFromPhone" }
    }

    override fun onDestroy() {
        info { "#onDestroy" }

        val currentChannel = activeChannel
        if (currentChannel == null) {
            warn { "Active channel was null" }
            return
        }
        runBlockingWithLog("close channel") {
            channelClient.close(currentChannel).await()
        }
    }
}
