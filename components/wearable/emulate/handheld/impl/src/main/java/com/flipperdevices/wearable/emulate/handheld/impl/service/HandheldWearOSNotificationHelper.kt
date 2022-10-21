package com.flipperdevices.wearable.emulate.handheld.impl.service

import com.flipperdevices.core.ui.res.R as DesignSystem
import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.flipperdevices.wearable.emulate.handheld.impl.R

private const val WEARABLE_NOTIFICATION_CHANNEL = "wearable_notification_channel"

object HandheldWearOSNotificationHelper {
    fun buildNotification(context: Context): Notification {
        createChannelIfNotYet(context)

        return NotificationCompat.Builder(context, WEARABLE_NOTIFICATION_CHANNEL)
            .setContentTitle(context.getString(R.string.wearable_notification_title))
            .setContentText(context.getString(R.string.wearable_notification_desc))
            .setSmallIcon(DesignSystem.drawable.ic_notification)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                DesignSystem.drawable.ic_close_icon,
                context.getString(R.string.wearable_notification_btn),
                WearableChannelCloseBroadcastReceiver.getCloseChannelIntent(context)
            ).build()
    }

    private fun createChannelIfNotYet(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)

        val flipperChannel = NotificationChannelCompat.Builder(
            WEARABLE_NOTIFICATION_CHANNEL,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(context.getString(R.string.wearable_notification_channel_title))
            .setDescription(context.getString(R.string.wearable_notification_channel_desc))
            .build()

        notificationManager.createNotificationChannel(flipperChannel)
    }
}
