package com.flipperdevices.wearable.emulate.handheld.impl.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class WearableChannelCloseBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val stopIntent = Intent(context, WearRequestListenerService::class.java).apply {
            action = WearRequestForegroundService.CLOSE_CHANNEL_ACTION
        }
        context.startService(stopIntent)
    }

    companion object {
        private const val ACTION =
            "com.flipperdevices.wearable.emulate.handheld.impl.service.CloseChannel"

        @SuppressLint("UnspecifiedImmutableFlag", "ObsoleteSdkInt")
        fun getCloseChannelIntent(context: Context): PendingIntent {
            val intent = Intent(context, WearableChannelCloseBroadcastReceiver::class.java)
            intent.action = ACTION

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }
        }
    }
}