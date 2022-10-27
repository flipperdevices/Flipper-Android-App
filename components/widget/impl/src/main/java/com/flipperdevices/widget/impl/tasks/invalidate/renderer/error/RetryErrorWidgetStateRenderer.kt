package com.flipperdevices.widget.impl.tasks.invalidate.renderer.error

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import androidx.annotation.StringRes
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.keyscreen.api.DeepLinkOpenKey
import com.flipperdevices.widget.impl.R
import com.flipperdevices.widget.impl.broadcast.WidgetBroadcastReceiver
import com.flipperdevices.widget.impl.tasks.invalidate.renderer.WidgetStateRenderer

abstract class RetryErrorWidgetStateRenderer(
    private val context: Context,
    private val deepLinkOpenKey: DeepLinkOpenKey,
    @StringRes private val errorTextId: Int
) : WidgetStateRenderer, LogTagProvider {
    override fun render(widgetId: Int, flipperKeyPath: FlipperKeyPath?): RemoteViews? {
        val keyPath = flipperKeyPath ?: return null

        return RemoteViews(context.packageName, R.layout.widget_layout_error).apply {
            setTextViewText(R.id.button_txt, context.getString(R.string.widget_err_retry_btn))
            setTextViewText(R.id.error_text, context.getString(errorTextId))

            val startIntent = WidgetBroadcastReceiver.buildStartIntent(
                context, keyPath, widgetId
            )
            setOnClickPendingIntent(R.id.button, startIntent)
            setOnClickPendingIntent(
                R.id.error_btn, PendingIntent.getActivity(
                    context,
                    widgetId,
                    deepLinkOpenKey.getIntentForOpenKey(flipperKeyPath),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
    }
}