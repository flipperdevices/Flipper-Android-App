package com.flipperdevices.bridge.connection.feature.networkproxy.impl.service

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info

class NetworkProxyForegroundService : Service(), LogTagProvider {
    override val TAG = "NetworkProxyForegroundService"

    override fun onCreate() {
        super.onCreate()
        info { "Service created" }
        try {
            startForeground(
                NOTIFICATION_ID,
                NetworkProxyNotificationHelper.buildNotification(applicationContext)
            )
        } catch (e: Exception) {
            error(e) { "Failed to start foreground service" }
            // On Android 12+, this can fail if started from background
            // The service will still run but without foreground priority
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                e is ForegroundServiceStartNotAllowedException) {
                // Stop self since we can't run as foreground
                stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        info { "Service received command: ${intent?.action}" }

        when (intent?.action) {
            ACTION_STOP -> {
                info { "Stopping service" }
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        info { "Service destroyed" }
    }

    companion object {
        private const val NOTIFICATION_ID = 101
        const val ACTION_STOP = "com.flipperdevices.networkproxy.STOP"

        fun start(context: Context) {
            val intent = Intent(context, NetworkProxyForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, NetworkProxyForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
