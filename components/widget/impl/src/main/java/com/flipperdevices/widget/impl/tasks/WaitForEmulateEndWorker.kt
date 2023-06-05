package com.flipperdevices.widget.impl.tasks

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.combine
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.protobuf.app.Application
import com.flipperdevices.widget.impl.di.WidgetComponent
import com.flipperdevices.widget.impl.tasks.invalidate.WidgetNotificationHelper
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class WaitForEmulateEndWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG = "WaitForEmulateEndWorker"

    @Inject
    lateinit var emulateHelper: EmulateHelper

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    init {
        ComponentHolder.component<WidgetComponent>().inject(this)
    }

    private val widgetNotificationHelper = WidgetNotificationHelper(context)

    override suspend fun doWork(): Result {
        waitEmulateEnd(serviceProvider.getServiceApi().requestApi)
        return Result.success()
    }

    private suspend fun waitEmulateEnd(
        requestApi: FlipperRequestApi
    ) = emulateHelper.getCurrentEmulatingKey()
        .combine(requestApi.notificationFlow())
        .filter { (currentEmulatingKey, unknownMessage) ->
            if (currentEmulatingKey == null) {
                return@filter true
            }
            if (unknownMessage.hasAppStateResponse()) {
                if (unknownMessage.appStateResponse.state == Application.AppState.APP_CLOSED) {
                    return@filter true
                }
            }
            return@filter false
        }.first()

    override suspend fun getForegroundInfo() = widgetNotificationHelper.emulatingForegroundInfo(id)
}
