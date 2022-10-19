package com.flipperdevices.widget.impl.tasks.invalidate

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.widget.impl.di.WidgetComponent
import javax.inject.Inject

class InvalidateWidgetsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG = "InvalidateWidgetsTask"

    @Inject
    lateinit var invalidateWidgetsHelper: InvalidateWidgetsHelper

    init {
        ComponentHolder.component<WidgetComponent>().inject(this)
    }

    private val widgetNotificationHelper = WidgetNotificationHelper(context)

    override suspend fun doWork(): Result {
        setForegroundAsync(widgetNotificationHelper.invalidateForegroundInfo(id))
        invalidateWidgetsHelper.invoke()
        return Result.success()
    }
}
