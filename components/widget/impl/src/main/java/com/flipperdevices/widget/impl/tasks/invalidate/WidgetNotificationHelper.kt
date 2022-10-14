package com.flipperdevices.widget.impl.tasks.invalidate

import com.flipperdevices.core.ui.res.R as DesignSystem
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import com.flipperdevices.widget.impl.R
import com.flipperdevices.widget.impl.model.Constants.WIDGET_NOTIFICATION_CHANNEL
import java.util.UUID

@SuppressLint("ObsoleteSdkInt")
class WidgetNotificationHelper(
    private val context: Context
) {
    fun invalidateForegroundInfo(workerId: UUID): ForegroundInfo {
        val title = context.getString(R.string.notification_widget_invalidate_name)
        val description = context.getString(R.string.notification_widget_invalidate_desc)
        return createForegroundInfo(title, description, workerId)
    }

    fun startForegroundInfo(workerId: UUID): ForegroundInfo {
        val title = context.getString(R.string.notification_widget_start_name)
        val description = context.getString(R.string.notification_widget_start_desc)
        return createForegroundInfo(title, description, workerId)
    }

    fun emulatingForegroundInfo(workerId: UUID): ForegroundInfo {
        val title = context.getString(R.string.notification_widget_emulating_name)
        val description = context.getString(R.string.notification_widget_emulating_desc)
        return createForegroundInfo(title, description, workerId)
    }

    fun stopForegroundInfo(workerId: UUID): ForegroundInfo {
        val title = context.getString(R.string.notification_widget_emulating_name)
        val description = context.getString(R.string.notification_widget_emulating_desc)
        return createForegroundInfo(title, description, workerId)
    }

    fun waitingFlipperForegroundInfo(workerId: UUID): ForegroundInfo {
        val title = context.getString(R.string.notification_widget_waiting_flipper_name)
        val description = context.getString(R.string.notification_widget_waiting_flipper_desc)
        return createForegroundInfo(title, description, workerId)
    }

    private fun createForegroundInfo(
        title: String,
        description: String,
        workerId: UUID
    ): ForegroundInfo {
        val cancel = context.getString(R.string.notification_widget_cancel)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(context)
            .createCancelPendingIntent(workerId)

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(context, WIDGET_NOTIFICATION_CHANNEL)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(description)
            .setSmallIcon(DesignSystem.drawable.ic_notification)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(workerId.hashCode(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val notificationManager = NotificationManagerCompat.from(context)

        val flipperChannel = NotificationChannelCompat.Builder(
            WIDGET_NOTIFICATION_CHANNEL,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(context.getString(R.string.notification_widget_channel_name))
            .setDescription(context.getString(R.string.notification_widget_channel_desc))
            .build()

        notificationManager.createNotificationChannel(flipperChannel)
    }
}