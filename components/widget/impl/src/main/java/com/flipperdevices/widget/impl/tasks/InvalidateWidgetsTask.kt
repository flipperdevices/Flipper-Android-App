package com.flipperdevices.widget.impl.tasks

import com.flipperdevices.core.ui.res.R as DesignSystem
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.widget.impl.R
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

interface InvalidateWidgetsTask {
    suspend fun invoke()
}

@ContributesBinding(AppGraph::class, InvalidateWidgetsTask::class)
class InvalidateWidgetsTaskImpl @Inject constructor(
    private val widgetDataApi: WidgetDataApi,
    private val context: Context,
    private val applicationParams: ApplicationParams
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

    private fun updateWidget(
        appWidgetId: Int,
        flipperKeyPath: FlipperKeyPath
    ) {
        info { "Update widget $appWidgetId" }
        val iconId = flipperKeyPath.path.keyType?.icon
            ?: DesignSystem.drawable.ic_fileformat_unknown
        val layoutId = when (flipperKeyPath.path.keyType) {
            FlipperKeyType.SUB_GHZ -> R.layout.widget_layout_send
            FlipperKeyType.RFID,
            FlipperKeyType.NFC,
            FlipperKeyType.I_BUTTON -> R.layout.widget_layout_emulate
            null,
            FlipperKeyType.INFRARED -> return
        }

        val view = RemoteViews(context.packageName, layoutId).apply {
            setTextViewText(R.id.widget_key_name, flipperKeyPath.path.nameWithoutExtension)
            setImageViewResource(R.id.widget_key_icon, iconId)
        }
        appWidgetManager.updateAppWidget(appWidgetId, view)
    }

    private fun setUpInitWidget(appWidgetId: Int) {
        info { "Create init widget for $appWidgetId" }
        val intent = Intent(context, applicationParams.startApplicationClass.java)
        val bundle = Bundle().apply {
            putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        intent.action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        val configurePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context, 0, intent, 0, bundle
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