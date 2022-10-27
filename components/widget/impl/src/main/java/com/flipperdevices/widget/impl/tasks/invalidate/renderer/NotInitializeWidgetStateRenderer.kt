package com.flipperdevices.widget.impl.tasks.invalidate.renderer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.widget.impl.R
import com.flipperdevices.widget.impl.model.WidgetRendererOf
import com.flipperdevices.widget.impl.model.WidgetState
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@WidgetRendererOf(WidgetState.NOT_INITIALIZE)
@ContributesMultibinding(AppGraph::class, WidgetStateRenderer::class)
class NotInitializeWidgetStateRenderer @Inject constructor(
    private val context: Context,
    private val applicationParams: ApplicationParams
) : WidgetStateRenderer, LogTagProvider {
    override val TAG = "NotInitializeWidgetStateRenderer"

    override fun render(widgetId: Int, flipperKeyPath: FlipperKeyPath?): RemoteViews {
        val intent = Intent(context, applicationParams.startApplicationClass.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

        val configurePendingIntent = PendingIntent.getActivity(
            context, widgetId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return RemoteViews(context.packageName, R.layout.initial_layout).apply {
            setOnClickPendingIntent(
                R.id.widget_root,
                configurePendingIntent
            )
        }
    }
}
