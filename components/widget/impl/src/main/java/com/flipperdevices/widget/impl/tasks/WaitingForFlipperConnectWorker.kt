package com.flipperdevices.widget.impl.tasks

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flipperdevices.bridge.api.manager.ktx.state.ConnectionState
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.widget.impl.di.WidgetComponent
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.invalidate.InvalidateWidgetsHelper
import com.flipperdevices.widget.impl.tasks.invalidate.WidgetNotificationHelper
import javax.inject.Inject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout

private const val WAIT_FLIPPER_TIMEOUT_MS = 60 * 1000L // 1 min
private const val DEFAULT_WIDGET_APP_ID = -1

class WaitingForFlipperConnectWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG = "WaitingForFlipperConnectWorker"

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var widgetStorage: WidgetStateStorage

    @Inject
    lateinit var invalidateWidgetsHelper: InvalidateWidgetsHelper

    init {
        ComponentHolder.component<WidgetComponent>().inject(this)
    }

    private val widgetNotificationHelper = WidgetNotificationHelper(context)

    override suspend fun doWork(): Result {
        val widgetId = inputData.getInt(EXTRA_KEY_WIDGET_ID, DEFAULT_WIDGET_APP_ID)
        if (widgetId < 0) {
            error("Widget id less then zero")
        }
        setForegroundAsync(widgetNotificationHelper.waitingFlipperForegroundInfo(id))
        val serviceApi = serviceProvider.getServiceApi()

        try {
            withTimeout(WAIT_FLIPPER_TIMEOUT_MS) {
                serviceApi.connectionInformationApi.getConnectionStateFlow().filter {
                    it is ConnectionState.Ready
                }.first()
            }
        } catch (timeout: TimeoutCancellationException) {
            error(timeout) { "Can't connect to flipper within $WAIT_FLIPPER_TIMEOUT_MS ms" }
            widgetStorage.updateState(widgetId, WidgetState.ERROR)
            invalidateWidgetsHelper.invoke()
            return Result.failure()
        }
        return Result.success()
    }
}
