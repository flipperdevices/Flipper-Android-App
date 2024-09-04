package com.flipperdevices.widget.impl.tasks.invalidate.renderer

import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.dao.api.model.icon
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.widget.impl.R
import com.flipperdevices.widget.impl.broadcast.WidgetBroadcastReceiver
import com.flipperdevices.widget.impl.model.WidgetRendererOf
import com.flipperdevices.widget.impl.model.WidgetState
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import com.flipperdevices.core.ui.res.R as DesignSystem

@WidgetRendererOf(WidgetState.PENDING)
@ContributesMultibinding(AppGraph::class, WidgetStateRenderer::class)
class PendingWidgetStateRenderer @Inject constructor(
    private val context: Context
) : WidgetStateRenderer, LogTagProvider {
    override val TAG = "PendingWidgetStateRenderer"

    override fun render(widgetId: Int, flipperKeyPath: FlipperKeyPath?): RemoteViews? {
        val keyPath = flipperKeyPath ?: return null
        val iconId = keyPath.path.keyType?.icon
            ?: DesignSystem.drawable.ic_fileformat_unknown
        val layoutId = when (flipperKeyPath.path.keyType) {
            FlipperKeyType.SUB_GHZ -> R.layout.widget_layout_send
            FlipperKeyType.RFID,
            FlipperKeyType.NFC,
            FlipperKeyType.I_BUTTON -> R.layout.widget_layout_emulate
            null,
            FlipperKeyType.INFRARED -> return null
        }
        return RemoteViews(context.packageName, layoutId).apply {
            setTextViewText(R.id.widget_key_name, flipperKeyPath.path.nameWithoutExtension)
            setImageViewResource(R.id.widget_key_icon, iconId)
            val startIntent = WidgetBroadcastReceiver.buildStartIntent(
                context,
                flipperKeyPath,
                widgetId
            )

            setOnClickPendingIntent(R.id.button, startIntent)
            setViewVisibility(R.id.progress_bar, View.GONE)
            setViewVisibility(R.id.progress_bar_indeterminate, View.GONE)
            setViewVisibility(R.id.error_btn, View.GONE)
        }
    }
}
