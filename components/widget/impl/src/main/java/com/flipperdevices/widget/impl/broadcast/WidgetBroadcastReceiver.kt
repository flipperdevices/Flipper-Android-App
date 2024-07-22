package com.flipperdevices.widget.impl.broadcast

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.WidgetType
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.widget.impl.di.WidgetComponent
import com.flipperdevices.widget.impl.model.WidgetState
import kotlinx.coroutines.runBlocking

private const val FLIPPER_KEY_START_ACTION = "com.flipperdevices.widget.impl.broadcast.KeyStart"
private const val FLIPPER_KEY_STOP_ACTION = "com.flipperdevices.widget.impl.broadcast.KeyStop"
private const val FLIPPER_KEY_PATH_KEY = "flipper_key_path"
private const val DEFAULT_WIDGET_APP_ID = -1

class WidgetBroadcastReceiver : BroadcastReceiver(), LogTagProvider {
    override val TAG = "WidgetBroadcastReceiver"

    private val widgetComponent by lazy { ComponentHolder.component<WidgetComponent>() }

    override fun onReceive(context: Context, intent: Intent?) {
        info { "Receive intent ${intent?.toFullString()}" }
        @Suppress("DEPRECATION")
        val keyPath = intent?.extras?.getParcelable<FlipperKeyPath>(FLIPPER_KEY_PATH_KEY) ?: return
        info { "Key path is $keyPath" }
        val widgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            DEFAULT_WIDGET_APP_ID
        ) ?: DEFAULT_WIDGET_APP_ID
        info { "Widget id is $widgetId" }
        if (widgetId == DEFAULT_WIDGET_APP_ID) {
            return
        }

        when (intent.action) {
            FLIPPER_KEY_START_ACTION -> {
                runBlocking {
                    widgetComponent.widgetStateStorage.updateState(
                        widgetId,
                        WidgetState.IN_PROGRESS
                    )
                }
                val widgetData = runBlocking {
                    widgetComponent.widgetDataApi.getWidgetDataByWidgetId(widgetId)
                }
                val oneTimeEmulation = widgetData?.widgetType == WidgetType.ONE_CLICK

                StartChainBuilder.getStartChain(
                    context = context,
                    filePath = keyPath.path,
                    widgetId = widgetId,
                    oneTimeEmulation = oneTimeEmulation
                ).enqueue()
            }

            FLIPPER_KEY_STOP_ACTION ->
                StopChainBuilder.getStopChain(context, widgetId).enqueue()
        }
    }

    companion object {
        @SuppressLint("UnspecifiedImmutableFlag")
        fun buildStartIntent(
            context: Context,
            flipperKeyPath: FlipperKeyPath,
            widgetAppId: Int
        ): PendingIntent {
            val intent = Intent(context, WidgetBroadcastReceiver::class.java)
            intent.action = FLIPPER_KEY_START_ACTION
            intent.putExtra(FLIPPER_KEY_PATH_KEY, flipperKeyPath)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetAppId)

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(
                    context,
                    widgetAppId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getBroadcast(
                    context,
                    widgetAppId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        fun buildStopIntent(
            context: Context,
            flipperKeyPath: FlipperKeyPath,
            widgetAppId: Int
        ): PendingIntent {
            val intent = Intent(context, WidgetBroadcastReceiver::class.java)
            intent.action = FLIPPER_KEY_STOP_ACTION
            intent.putExtra(FLIPPER_KEY_PATH_KEY, flipperKeyPath)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetAppId)

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(
                    context,
                    widgetAppId,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                PendingIntent.getBroadcast(
                    context,
                    widgetAppId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }
    }
}
