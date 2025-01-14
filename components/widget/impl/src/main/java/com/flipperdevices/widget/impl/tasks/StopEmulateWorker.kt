package com.flipperdevices.widget.impl.tasks

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.withCoroutineScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.widget.impl.di.WidgetComponent
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.invalidate.WidgetNotificationHelper
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val DEFAULT_WIDGET_APP_ID = -1

class StopEmulateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG = "StopEmulateWorker"

    @Inject
    lateinit var fFeatureProvider: FFeatureProvider

    @Inject
    lateinit var widgetStateStorage: WidgetStateStorage

    init {
        ComponentHolder.component<WidgetComponent>().inject(this)
    }

    private val widgetNotificationHelper = WidgetNotificationHelper(context)

    override suspend fun doWork(): Result = withCoroutineScope { scope ->
        val widgetId = inputData.getInt(EXTRA_KEY_WIDGET_ID, DEFAULT_WIDGET_APP_ID)
        if (widgetId < 0) {
            error("Widget id less then zero")
        }
        val fEmulateApi = fFeatureProvider.getSync<FEmulateFeatureApi>() ?: run {
            error { "#onStartEmulateInternal could not get emulate api" }
            return@withCoroutineScope Result.failure()
        }
        val emulateHelper = fEmulateApi.getEmulateHelper()

        emulateHelper.stopEmulate(scope)
        emulateHelper.getCurrentEmulatingKey().filter { it == null }.first()
        widgetStateStorage.updateState(widgetId, WidgetState.PENDING)
        return@withCoroutineScope Result.success()
    }

    override suspend fun getForegroundInfo() = widgetNotificationHelper.stopForegroundInfo(id)
}
