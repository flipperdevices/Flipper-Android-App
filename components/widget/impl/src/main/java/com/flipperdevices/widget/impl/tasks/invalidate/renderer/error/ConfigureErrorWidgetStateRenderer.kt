package com.flipperdevices.widget.impl.tasks.invalidate.renderer.error

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.StringRes
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.keyscreen.api.DeepLinkOpenKey
import com.flipperdevices.widget.impl.R
import com.flipperdevices.widget.impl.tasks.invalidate.renderer.WidgetStateRenderer

abstract class ConfigureErrorWidgetStateRenderer(
    private val context: Context,
    private val applicationParams: ApplicationParams,
    private val deepLinkOpenKey: DeepLinkOpenKey,
    @StringRes private val errorTextId: Int
) : WidgetStateRenderer, LogTagProvider {
    override fun render(widgetId: Int, flipperKeyPath: FlipperKeyPath?): RemoteViews? {
        val keyPath = flipperKeyPath ?: return null

        val intent = Intent(context, applicationParams.startApplicationClass.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

        val configurePendingIntent = PendingIntent.getActivity(
            context, widgetId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return RemoteViews(context.packageName, R.layout.widget_layout_error).apply {
            setTextViewText(R.id.button_txt, context.getText(R.string.widget_err_customize_btn))
            setTextViewText(R.id.error_text, context.getString(errorTextId))

            setOnClickPendingIntent(R.id.button, configurePendingIntent)
            setOnClickPendingIntent(
                R.id.error_btn,
                PendingIntent.getActivity(
                    context,
                    widgetId,
                    deepLinkOpenKey.getIntentForOpenKey(keyPath),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
    }
}