package com.flipperdevices.bridge.connection.feature.networkproxy.impl.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.flipperdevices.bridge.connection.feature.networkproxy.impl.R
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val NETWORK_PROXY_NOTIFICATION_CHANNEL = "network_proxy_channel"

object NetworkProxyNotificationHelper {
    fun buildNotification(context: Context): Notification {
        createChannelIfNotYet(context)

        val stopIntent = Intent(context, NetworkProxyForegroundService::class.java).apply {
            action = NetworkProxyForegroundService.ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, NETWORK_PROXY_NOTIFICATION_CHANNEL)
            .setContentTitle(context.getString(R.string.network_proxy_notification_title))
            .setContentText(context.getString(R.string.network_proxy_notification_desc))
            .setSmallIcon(DesignSystem.drawable.ic_notification)
            .setSilent(true)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(
                DesignSystem.drawable.ic_close_icon,
                context.getString(R.string.network_proxy_notification_stop),
                stopPendingIntent
            ).build()
    }

    private fun createChannelIfNotYet(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)

        val channel = NotificationChannelCompat.Builder(
            NETWORK_PROXY_NOTIFICATION_CHANNEL,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(context.getString(R.string.network_proxy_channel_name))
            .setDescription(context.getString(R.string.network_proxy_channel_desc))
            .build()

        notificationManager.createNotificationChannel(channel)
    }
}
