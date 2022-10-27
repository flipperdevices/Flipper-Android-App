package com.flipperdevices.widget.impl.tasks.invalidate.renderer

import android.widget.RemoteViews
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath

interface WidgetStateRenderer {
    fun render(
        widgetId: Int,
        flipperKeyPath: FlipperKeyPath? = null
    ): RemoteViews?
}