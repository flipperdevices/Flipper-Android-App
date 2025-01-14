package com.flipperdevices.widget.impl.tasks

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flipperdevices.bridge.connection.feature.emulate.api.FEmulateFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureStatus
import com.flipperdevices.bridge.connection.feature.provider.api.get
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.protobuf.app.AppState
import com.flipperdevices.widget.impl.di.WidgetComponent
import com.flipperdevices.widget.impl.tasks.invalidate.WidgetNotificationHelper
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WaitForEmulateEndWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG = "WaitForEmulateEndWorker"

    @Inject
    lateinit var fFeatureProvider: FFeatureProvider

    init {
        ComponentHolder.component<WidgetComponent>().inject(this)
    }

    private val widgetNotificationHelper = WidgetNotificationHelper(context)

    override suspend fun doWork(): Result {
        waitEmulateEnd()
        return Result.success()
    }

    private suspend fun waitEmulateEnd() = combine(
        flow = fFeatureProvider.get<FEmulateFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported<FEmulateFeatureApi> }
            .map { status -> status?.featureApi }
            .flatMapLatest { feature ->
                feature?.getEmulateHelper()?.getCurrentEmulatingKey() ?: flowOf(null)
            },
        flow2 = fFeatureProvider.get<FEmulateFeatureApi>()
            .map { status -> status as? FFeatureStatus.Supported<FEmulateFeatureApi> }
            .map { status -> status?.featureApi }
            .flatMapLatest { feature ->
                feature?.getAppEmulateHelper()?.appStateFlow() ?: flowOf(null)
            },
        transform = { currentEmulatingKey, appStateResponse ->
            currentEmulatingKey to appStateResponse
        }

    ).filter { (currentEmulatingKey, appStateResponse) ->
        return@filter when {
            currentEmulatingKey == null -> true
            appStateResponse?.state == AppState.APP_CLOSED -> true
            else -> false
        }
    }.first()

    override suspend fun getForegroundInfo() = widgetNotificationHelper.emulatingForegroundInfo(id)
}
