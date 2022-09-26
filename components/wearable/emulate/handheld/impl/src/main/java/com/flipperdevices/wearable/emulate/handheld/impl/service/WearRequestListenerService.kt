package com.flipperdevices.wearable.emulate.handheld.impl.service

import androidx.lifecycle.lifecycleScope
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.wearable.emulate.handheld.impl.di.DaggerWearServiceComponent
import com.google.android.gms.wearable.ChannelClient

class WearRequestListenerService :
    LifecycleWearableListenerService(),
    LogTagProvider {
    override val TAG = "WearRequestListenerService"

    private val wearServiceComponent by lazy {
        DaggerWearServiceComponent.factory().create(
            ComponentHolder.component(), this
        )
    }

    init {
        wearServiceComponent.commandProcessors.forEach { it.init() }
    }

    override fun onChannelOpened(channel: ChannelClient.Channel) {
        super.onChannelOpened(channel)
        wearServiceComponent.commandInputStream.onOpenChannel(lifecycleScope, channel)
        wearServiceComponent.commandOutputStream.onOpenChannel(lifecycleScope, channel)
    }

    override fun onChannelClosed(
        channel: ChannelClient.Channel,
        closeReason: Int,
        appSpecificErrorCode: Int
    ) {
        super.onChannelClosed(channel, closeReason, appSpecificErrorCode)
        wearServiceComponent.commandInputStream.onCloseChannel(lifecycleScope)
        wearServiceComponent.commandOutputStream.onCloseChannel(lifecycleScope)
    }

}
