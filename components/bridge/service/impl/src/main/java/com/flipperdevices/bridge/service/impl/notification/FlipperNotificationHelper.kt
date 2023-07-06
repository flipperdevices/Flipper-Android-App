package com.flipperdevices.bridge.service.impl.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.flipperdevices.bridge.service.impl.R
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.ui.res.R as DesignSystem

private const val FLIPPER_NOTIFICATION_CHANNEL = "flipper_service"
const val FLIPPER_NOTIFICATION_ID = 1

class FlipperNotificationHelper(
    private val context: Context,
    private val applicationParams: ApplicationParams
) {
    private val notificationBuilder =
        NotificationCompat.Builder(context, FLIPPER_NOTIFICATION_CHANNEL)
            .setContentTitle(context.getString(R.string.bridge_service_notification_title))
            .setContentText(context.getString(R.string.bridge_service_notification_desc))
            .setSmallIcon(DesignSystem.drawable.ic_notification)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getIntentForOpenApplication())
    private val notificationManager = NotificationManagerCompat.from(context)

    fun showStopButton() {
        notificationBuilder.addAction(
            DesignSystem.drawable.ic_disconnect,
            context.getString(R.string.bridge_service_notification_action_disconnect),
            FlipperDisconnectBroadcastReceiver.getDisconnectIntent(context)
        )
        buildAndNotify()
    }

    fun show(): Notification {
        return buildAndNotify()
    }

    private fun buildAndNotify(): Notification {
        createChannelIfNotYet(context)

        val notification = notificationBuilder.build()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(FLIPPER_NOTIFICATION_ID, notification)
        }
        return notification
    }

    private fun createChannelIfNotYet(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)

        val flipperChannel = NotificationChannelCompat.Builder(
            FLIPPER_NOTIFICATION_CHANNEL,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(context.getString(R.string.bridge_service_notification_channel_name))
            .setDescription(context.getString(R.string.bridge_service_notification_channel_desc))
            .build()

        notificationManager.createNotificationChannel(flipperChannel)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getIntentForOpenApplication(): PendingIntent {
        val intent = Intent(context, applicationParams.startApplicationClass.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(context, 0, intent, 0)
        }
    }
}
