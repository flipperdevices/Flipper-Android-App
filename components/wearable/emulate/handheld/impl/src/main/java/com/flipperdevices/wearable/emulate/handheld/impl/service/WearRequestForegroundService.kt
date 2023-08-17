package com.flipperdevices.wearable.emulate.handheld.impl.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.handheld.impl.di.DaggerWearServiceComponent
import com.google.android.gms.wearable.ChannelClient.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus

private const val NOTIFICATION_ID = 100

class WearRequestForegroundService : LifecycleService(), WearRequestChannelBinder, LogTagProvider {
    override val TAG = "WearRequestForegroundService"
    private val wearServiceComponent = DaggerWearServiceComponent.factory().create(
        ComponentHolder.component(),
        lifecycleScope + Dispatchers.Default
    )
    private val binder = WearRequestBinder(this)

    override fun onCreate() {
        super.onCreate()
        info { "#onCreate" }
        startForeground(
            NOTIFICATION_ID,
            HandheldWearOSNotificationHelper.buildNotification(applicationContext)
        )
        wearServiceComponent.commandProcessors.forEach { it.init() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        info { "Service receive command with action ${intent?.action}" }

        if (intent?.action == CLOSE_CHANNEL_ACTION) {
            wearServiceComponent.commandInputStream.onCloseChannel(lifecycleScope)
            wearServiceComponent.commandOutputStream.onCloseChannel(lifecycleScope)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        info { "On bind $intent" }
        return binder
    }

    override fun onChannelOpen(channel: Channel) {
        info { "#onChannelOpen" }
        wearServiceComponent.commandInputStream.onOpenChannel(lifecycleScope, channel)
        wearServiceComponent.commandOutputStream.onOpenChannel(lifecycleScope, channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        info { "#onDestroy" }
    }

    companion object {
        const val CLOSE_CHANNEL_ACTION =
            "com.flipperdevices.wearable.emulate.handheld.impl.service.ServiceClose"
    }
}

interface WearRequestChannelBinder {
    fun onChannelOpen(channel: Channel)
}

class WearRequestBinder internal constructor(
    val channelBinder: WearRequestChannelBinder
) : Binder()
