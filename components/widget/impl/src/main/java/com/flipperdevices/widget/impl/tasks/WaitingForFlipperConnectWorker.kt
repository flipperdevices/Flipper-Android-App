package com.flipperdevices.widget.impl.tasks

import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flipperdevices.bridge.connection.orchestrator.api.FDeviceOrchestrator
import com.flipperdevices.bridge.connection.orchestrator.api.model.FDeviceConnectStatus
import com.flipperdevices.bridge.connection.service.api.FConnectionService
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.getBluetoothAdapter
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.widget.impl.di.WidgetComponent
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.invalidate.InvalidateWidgetsHelper
import com.flipperdevices.widget.impl.tasks.invalidate.WidgetNotificationHelper
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

private const val WAIT_FLIPPER_TIMEOUT_MS = 3 * 1000L // 10 sec
private const val DEFAULT_WIDGET_APP_ID = -1

class WaitingForFlipperConnectWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG = "WaitingForFlipperConnectWorker"

    @Inject
    lateinit var widgetStorage: WidgetStateStorage

    @Inject
    lateinit var invalidateWidgetsHelper: InvalidateWidgetsHelper

    @Inject
    lateinit var fConnectionService: FConnectionService

    @Inject
    lateinit var fDeviceOrchestrator: FDeviceOrchestrator

    init {
        ComponentHolder.component<WidgetComponent>().inject(this)
    }

    private val widgetNotificationHelper = WidgetNotificationHelper(context)

    override suspend fun doWork(): Result {
        val widgetId = inputData.getInt(EXTRA_KEY_WIDGET_ID, DEFAULT_WIDGET_APP_ID)
        if (widgetId < 0) {
            error("Widget id less then zero")
        }

        if (!isBluetoothEnabled()) {
            error { "Failed emulate because bt not enabled" }
            widgetStorage.updateState(widgetId, WidgetState.ERROR_BT_NOT_ENABLED)
            invalidateWidgetsHelper.invoke()
            return Result.failure()
        }

        try {
            fConnectionService.connectIfNotForceDisconnect()
            withTimeout(WAIT_FLIPPER_TIMEOUT_MS) {
                fDeviceOrchestrator.getState()
                    .filterIsInstance<FDeviceConnectStatus.Connected>()
                    .first()
            }
        } catch (timeout: TimeoutCancellationException) {
            error(timeout) { "Can't connect to flipper within $WAIT_FLIPPER_TIMEOUT_MS ms" }
            widgetStorage.updateState(widgetId, WidgetState.ERROR_OUT_OF_RANGE)
            invalidateWidgetsHelper.invoke()
            return Result.failure()
        }
        return Result.success()
    }

    override suspend fun getForegroundInfo() =
        widgetNotificationHelper.waitingFlipperForegroundInfo(id)

    private fun isBluetoothEnabled(): Boolean {
        val bluetoothManager = ContextCompat.getSystemService(
            applicationContext,
            BluetoothManager::class.java
        )
        val adapter = bluetoothManager?.getBluetoothAdapter()
        return adapter?.isEnabled ?: false
    }
}
