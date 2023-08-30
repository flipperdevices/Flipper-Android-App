package com.flipperdevices.wearable.emulate.handheld.impl.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.WearableListenerService

class WearRequestListenerService :
    WearableListenerService(),
    LogTagProvider,
    ServiceConnection {
    override val TAG = "WearRequestListenerService-${hashCode()}"

    private var pendingChannel: ChannelClient.Channel? = null

    override fun onChannelOpened(channel: ChannelClient.Channel) {
        super.onChannelOpened(channel)
        info { "#onChannelOpened $channel" }
        pendingChannel = channel
        // Without it service destroy after unbind
        applicationContext.startService(
            Intent(
                applicationContext,
                WearRequestForegroundService::class.java
            )
        )
        val bindSuccessful = applicationContext.bindService(
            Intent(applicationContext, WearRequestForegroundService::class.java),
            this,
            Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT
        )
        info { "Bind for service is $bindSuccessful" }
    }

    override fun onChannelClosed(
        channel: ChannelClient.Channel,
        closeReason: Int,
        appSpecificErrorCode: Int
    ) {
        super.onChannelClosed(channel, closeReason, appSpecificErrorCode)
        info { "#onChannelClosed $channel Reason: $closeReason AppCode: $appSpecificErrorCode" }

        val intent = Intent(applicationContext, WearRequestForegroundService::class.java)
        intent.action = WearRequestForegroundService.CLOSE_CHANNEL_ACTION
        applicationContext.startService(intent)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val serviceBinder = service as? WearRequestBinder ?: return
        val channel = pendingChannel ?: return

        serviceBinder.channelBinder.onChannelOpen(channel)
    }

    override fun onServiceDisconnected(name: ComponentName?) = Unit

    override fun onDestroy() {
        super.onDestroy()
        info { "#onDestroy" }
    }
}
