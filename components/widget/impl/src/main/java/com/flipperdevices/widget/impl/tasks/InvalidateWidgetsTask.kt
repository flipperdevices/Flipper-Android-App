package com.flipperdevices.widget.impl.tasks

import com.flipperdevices.core.ui.res.R as DesignSystem
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.widget.impl.R
import com.flipperdevices.widget.impl.broadcast.WidgetBroadcastReceiver
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface InvalidateWidgetsTask {
    suspend fun invoke()
}

@ContributesBinding(AppGraph::class, InvalidateWidgetsTask::class)
class InvalidateWidgetsTaskImpl @Inject constructor(
    private val widgetDataApi: WidgetDataApi,
    private val context: Context,
    private val applicationParams: ApplicationParams,
    private val widgetStateStorage: WidgetStateStorage
) : InvalidateWidgetsTask, LogTagProvider {
    override val TAG = "InvalidateWidgetsTask"

    private val appWidgetManager by lazy { AppWidgetManager.getInstance(context) }

    override suspend fun invoke() {
        info { "Invoke invalidate widgets" }
        val widgets = widgetDataApi.getAll()
        info { "Receive $widgets" }
        widgets.forEach {
            info { "Invalidate for $it widget" }
            val keyPath = it.flipperKeyPath
            if (keyPath == null) {
                setUpInitWidget(it.widgetId)
            } else updateWidget(it.widgetId, keyPath)
        }
    }

    private suspend fun updateWidget(
        appWidgetId: Int,
        flipperKeyPath: FlipperKeyPath
    ) {
        info { "Update widget $appWidgetId" }
        val iconId = flipperKeyPath.path.keyType?.icon
            ?: DesignSystem.drawable.ic_fileformat_unknown
        val widgetState = widgetStateStorage.getState(appWidgetId)
        val layoutId = when (flipperKeyPath.path.keyType) {
            FlipperKeyType.SUB_GHZ -> when (widgetState) {
                WidgetState.PENDING -> R.layout.widget_layout_send
                WidgetState.IN_PROGRESS -> R.layout.widget_layout_sending
            }
            FlipperKeyType.RFID,
            FlipperKeyType.NFC,
            FlipperKeyType.I_BUTTON -> when (widgetState) {
                WidgetState.PENDING -> R.layout.widget_layout_emulate
                WidgetState.IN_PROGRESS -> R.layout.widget_layout_emulating
            }
            null,
            FlipperKeyType.INFRARED -> return
        }

        val view = RemoteViews(context.packageName, layoutId).apply {
            setTextViewText(R.id.widget_key_name, flipperKeyPath.path.nameWithoutExtension)
            setImageViewResource(R.id.widget_key_icon, iconId)
            val stopIntent = WidgetBroadcastReceiver.buildStopIntent(
                context,
                flipperKeyPath,
                appWidgetId
            )
            setOnClickPendingIntent(R.id.progress_stop, stopIntent)
            when (widgetState) {
                WidgetState.PENDING -> {
                    setOnClickPendingIntent(
                        R.id.button,
                        WidgetBroadcastReceiver.buildStartIntent(
                            context,
                            flipperKeyPath,
                            appWidgetId
                        )
                    )
                    setViewVisibility(R.id.progress_bar, View.GONE)
                    setViewVisibility(R.id.progress_bar_indeterminate, View.GONE)
                }
                WidgetState.IN_PROGRESS -> {
                    setOnClickPendingIntent(
                        R.id.button,
                        WidgetBroadcastReceiver.buildStopIntent(
                            context,
                            flipperKeyPath,
                            appWidgetId
                        )
                    )
                    setViewVisibility(R.id.progress_bar, View.VISIBLE)
                    setViewVisibility(R.id.progress_bar_indeterminate, View.VISIBLE)
                }
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, view)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setUpInitWidget(appWidgetId: Int) {
        info { "Create init widget for $appWidgetId" }
        val intent = Intent(context, applicationParams.startApplicationClass.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        val configurePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context, 0, intent, 0
            )
        }
        val view = RemoteViews(context.packageName, R.layout.initial_layout).apply {
            setOnClickPendingIntent(
                R.id.widget_root,
                configurePendingIntent
            )
        }
        appWidgetManager.updateAppWidget(appWidgetId, view)
    }
}