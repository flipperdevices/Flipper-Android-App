package com.flipperdevices.widget.impl.broadcast

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.widget.impl.di.WidgetComponent
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.tasks.EXTRA_KEY_FILE_PATH
import com.flipperdevices.widget.impl.tasks.EXTRA_KEY_WIDGET_ID
import com.flipperdevices.widget.impl.tasks.StartEmulateWorker
import com.flipperdevices.widget.impl.tasks.StopEmulateWorker
import com.flipperdevices.widget.impl.tasks.WaitForEmulateEndWorker
import com.flipperdevices.widget.impl.tasks.WaitingForFlipperConnectWorker
import com.flipperdevices.widget.impl.tasks.invalidate.InvalidateWidgetsWorker
import kotlinx.coroutines.runBlocking

private const val FLIPPER_KEY_START_ACTION = "com.flipperdevices.widget.impl.broadcast.KeyStart"
private const val FLIPPER_KEY_STOP_ACTION = "com.flipperdevices.widget.impl.broadcast.KeyStop"
private const val FLIPPER_KEY_PATH_KEY = "flipper_key_path"
private const val DEFAULT_WIDGET_APP_ID = -1

private const val WORK_CHAIN_START_NAME = "start_emulating"
private const val WORK_CHAIN_STOP_NAME = "stop_emulating"

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

                getStartChain(context, keyPath.path, widgetId).enqueue()
            }
            FLIPPER_KEY_STOP_ACTION -> {
                getStopChain(context, widgetId).enqueue()
            }
        }
    }

    private fun getStopChain(
        context: Context,
        widgetId: Int
    ) = WorkManager.getInstance(context)
        .beginUniqueWork(
            WORK_CHAIN_STOP_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(InvalidateWidgetsWorker::class.java)
        )
        .then(
            OneTimeWorkRequestBuilder<StopEmulateWorker>().setInputData(
                Data.Builder()
                    .putInt(EXTRA_KEY_WIDGET_ID, widgetId)
                    .build()
            ).build()
        )
        .then(OneTimeWorkRequest.from(InvalidateWidgetsWorker::class.java))

    private fun getStartChain(
        context: Context,
        filePath: FlipperFilePath,
        widgetId: Int
    ) = WorkManager.getInstance(context)
        .beginUniqueWork(
            WORK_CHAIN_START_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(InvalidateWidgetsWorker::class.java)
        )
        .then(
            OneTimeWorkRequestBuilder<WaitingForFlipperConnectWorker>()
                .setInputData(
                    Data.Builder()
                        .putInt(EXTRA_KEY_WIDGET_ID, widgetId)
                        .build()
                ).build()
        )
        .then(
            OneTimeWorkRequestBuilder<StartEmulateWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(EXTRA_KEY_FILE_PATH, filePath.pathToKey)
                        .putInt(EXTRA_KEY_WIDGET_ID, widgetId)
                        .build()
                )
                .build()
        )
        .then(OneTimeWorkRequest.from(InvalidateWidgetsWorker::class.java))
        .then(OneTimeWorkRequest.from(WaitForEmulateEndWorker::class.java))
        .then(
            OneTimeWorkRequestBuilder<StopEmulateWorker>().setInputData(
                Data.Builder()
                    .putInt(EXTRA_KEY_WIDGET_ID, widgetId)
                    .build()
            ).build()
        )
        .then(OneTimeWorkRequest.from(InvalidateWidgetsWorker::class.java))

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
                    context, widgetAppId, intent,
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
                    context, widgetAppId, intent,
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
