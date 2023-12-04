package com.flipperdevices.updater.impl.service

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import com.flipperdevices.updater.impl.R
import java.util.UUID
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val UPDATE_NOTIFICATION_CHANNEL = "update_notification_channel"

object UpdaterNotification {
    fun getForegroundInfo(id: UUID, context: Context): ForegroundInfo {
        val intent = WorkManager.getInstance(context).createCancelPendingIntent(id)

        createNotificationChannel(context)

        val cancelButton = context.getString(R.string.update_notification_cancel)
        val title = context.getString(R.string.update_notification_title)
        val description = context.getString(R.string.update_notification_desc)

        val notification = NotificationCompat.Builder(context, UPDATE_NOTIFICATION_CHANNEL)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(description)
            .setSmallIcon(DesignSystem.drawable.ic_notification)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, cancelButton, intent)
            .build()

        return ForegroundInfo(id.hashCode(), notification)
    }

    private fun createNotificationChannel(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)

        val flipperChannel = NotificationChannelCompat.Builder(
            UPDATE_NOTIFICATION_CHANNEL,
            NotificationManagerCompat.IMPORTANCE_LOW
        )
            .setName(context.getString(R.string.update_notification_channel_title))
            .setDescription(context.getString(R.string.update_notification_channel_desc))
            .build()
        notificationManager.createNotificationChannel(flipperChannel)
    }
}
