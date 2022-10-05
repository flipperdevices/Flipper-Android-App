package com.flipperdevices.widget.impl.tasks

import com.flipperdevices.core.ui.res.R as DesignSystem
import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.widget.impl.R
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface InvalidateWidgetsTask {
    suspend fun invoke()
}

@ContributesBinding(AppGraph::class, InvalidateWidgetsTask::class)
class InvalidateWidgetsTaskImpl @Inject constructor(
    private val widgetDataApi: WidgetDataApi,
    private val context: Context
) : InvalidateWidgetsTask {
    private val appWidgetManager by lazy { AppWidgetManager.getInstance(context) }

    override suspend fun invoke() {
        widgetDataApi.getAll().forEach {
            val keyPath = it.flipperKeyPath ?: return@forEach
            updateWidget(it.widgetId, keyPath)
        }
    }

    private suspend fun updateWidget(
        appWidgetId: Int,
        flipperKeyPath: FlipperKeyPath
    ) = withContext(Dispatchers.Main) {
        val iconId = flipperKeyPath.path.keyType?.icon
            ?: DesignSystem.drawable.ic_fileformat_unknown
        val layoutId = when (flipperKeyPath.path.keyType) {
            FlipperKeyType.SUB_GHZ -> R.layout.widget_layout_send
            FlipperKeyType.RFID,
            FlipperKeyType.NFC,
            FlipperKeyType.I_BUTTON -> R.layout.widget_layout_emulate
            null,
            FlipperKeyType.INFRARED -> return@withContext
        }

        val view = RemoteViews(context.packageName, layoutId).apply {
            setTextViewText(R.id.widget_key_name, flipperKeyPath.path.nameWithoutExtension)
            setImageViewResource(R.id.widget_key_icon, iconId)
        }
        appWidgetManager.updateAppWidget(appWidgetId, view)
    }
}