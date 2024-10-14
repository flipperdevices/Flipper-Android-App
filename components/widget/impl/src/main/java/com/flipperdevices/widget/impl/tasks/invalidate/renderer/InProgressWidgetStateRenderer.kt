package com.flipperdevices.widget.impl.tasks.invalidate.renderer

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.iconId
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.widget.impl.R
import com.flipperdevices.widget.impl.broadcast.WidgetBroadcastReceiver
import com.flipperdevices.widget.impl.model.WidgetRendererOf
import com.flipperdevices.widget.impl.model.WidgetState
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

@WidgetRendererOf(WidgetState.IN_PROGRESS)
@ContributesMultibinding(AppGraph::class, WidgetStateRenderer::class)
class InProgressWidgetStateRenderer @Inject constructor(
    private val context: Context
) : WidgetStateRenderer, LogTagProvider {
    override val TAG = "InProgressWidgetStateRenderer"

    override fun render(widgetId: Int, flipperKeyPath: FlipperKeyPath?): RemoteViews? {
        val keyPath = flipperKeyPath ?: return null
        val iconId = keyPath.path.keyType?.iconId
            ?: DesignSystem.drawable.ic_fileformat_unknown
        val layoutId = when (flipperKeyPath.path.keyType) {
            FlipperKeyType.SUB_GHZ -> R.layout.widget_layout_sending
            FlipperKeyType.RFID,
            FlipperKeyType.NFC,
            FlipperKeyType.I_BUTTON -> R.layout.widget_layout_emulating
            null,
            FlipperKeyType.INFRARED -> return null
        }

        return RemoteViews(context.packageName, layoutId).apply {
            setTextViewText(R.id.widget_key_name, flipperKeyPath.path.nameWithoutExtension)
            setImageViewResource(R.id.widget_key_icon, iconId)
            val stopIntent = WidgetBroadcastReceiver.buildStopIntent(
                context,
                keyPath,
                widgetId
            )
            setOnClickPendingIntent(R.id.progress_stop, stopIntent)

            setOnClickPendingIntent(
                R.id.button,
                WidgetBroadcastReceiver.buildStopIntent(
                    context,
                    keyPath,
                    widgetId
                )
            )
            setViewVisibility(R.id.progress_bar, View.VISIBLE)
            setViewVisibility(R.id.progress_bar_indeterminate, View.VISIBLE)
            setViewVisibility(R.id.error_btn, View.GONE)
        }
    }
}
