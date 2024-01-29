package com.flipperdevices.bridge.service.impl.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.flipperdevices.bridge.service.impl.FlipperService

class FlipperDisconnectBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val stopIntent = Intent(context, FlipperService::class.java).apply {
            action = FlipperService.ACTION_STOP
        }
        context.startService(stopIntent)
    }

    companion object {
        private const val ACTION =
            "com.flipperdevices.bridge.service.impl.notification.DisconnectBroadcastReceiver"

        @SuppressLint("UnspecifiedImmutableFlag")
        fun getDisconnectIntent(context: Context): PendingIntent {
            val intent = Intent(context, FlipperDisconnectBroadcastReceiver::class.java)
            intent.action = ACTION

            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
    }
}
