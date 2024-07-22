package com.flipperdevices.widget.impl.tasks

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.withCoroutineScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyemulate.api.EmulateHelper
import com.flipperdevices.keyemulate.exception.AlreadyOpenedAppException
import com.flipperdevices.keyemulate.model.EmulateConfig
import com.flipperdevices.widget.impl.di.WidgetComponent
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.invalidate.InvalidateWidgetsHelper
import com.flipperdevices.widget.impl.tasks.invalidate.WidgetNotificationHelper
import kotlinx.coroutines.CoroutineScope
import java.io.File
import javax.inject.Inject

const val EXTRA_KEY_FILE_PATH = "file_path"
const val EXTRA_KEY_WIDGET_ID = AppWidgetManager.EXTRA_APPWIDGET_ID
private const val DEFAULT_WIDGET_APP_ID = -1

class StartEmulateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG = "StartEmulateWorker"

    @Inject
    lateinit var serviceApiProvider: FlipperServiceProvider

    @Inject
    lateinit var emulateHelper: EmulateHelper

    @Inject
    lateinit var widgetStateStorage: WidgetStateStorage

    @Inject
    lateinit var invalidateWidgetsHelper: InvalidateWidgetsHelper

    @Inject
    lateinit var simpleKeyApi: SimpleKeyApi

    init {
        ComponentHolder.component<WidgetComponent>().inject(this)
    }

    private val widgetNotificationHelper = WidgetNotificationHelper(context)

    override suspend fun doWork(): Result = withCoroutineScope { scope ->
        val widgetId = inputData.getInt(EXTRA_KEY_WIDGET_ID, DEFAULT_WIDGET_APP_ID)
        if (widgetId < 0) {
            error("Widget id less then zero")
        }
        // Start emulate
        val serviceApi = serviceApiProvider.getServiceApi()
        try {
            val filePath = getFilePath()

            if (!isSynced(filePath)) {
                info { "File not synced" }
                widgetStateStorage.updateState(widgetId, WidgetState.ERROR_NOT_SYNCED)
                invalidateWidgetsHelper.invoke()
                return@withCoroutineScope Result.failure()
            }

            if (!startEmulate(scope, serviceApi, filePath)) {
                widgetStateStorage.updateState(widgetId, WidgetState.ERROR_UNKNOWN)
                invalidateWidgetsHelper.invoke()
                return@withCoroutineScope Result.failure()
            }
        } catch (flipperBusy: AlreadyOpenedAppException) {
            error(flipperBusy) { "Flipper busy $inputData" }
            widgetStateStorage.updateState(widgetId, WidgetState.ERROR_FLIPPER_BUSY)
            invalidateWidgetsHelper.invoke()
            return@withCoroutineScope Result.failure()
        } catch (throwable: Throwable) {
            error(throwable) { "Failed emulate $inputData" }
            widgetStateStorage.updateState(widgetId, WidgetState.ERROR_UNKNOWN)
            invalidateWidgetsHelper.invoke()
            return@withCoroutineScope Result.failure()
        }
        widgetStateStorage.updateState(widgetId, WidgetState.IN_PROGRESS)
        invalidateWidgetsHelper.invoke()

        return@withCoroutineScope Result.success()
    }

    private suspend fun startEmulate(
        scope: CoroutineScope,
        serviceApi: FlipperServiceApi,
        filePath: FlipperFilePath
    ): Boolean {
        info { "Start emulate" }
        val keyType = filePath.keyType ?: error("Not found key type")
        val emulateConfig = EmulateConfig(keyType, filePath)
        return emulateHelper.startEmulate(scope, serviceApi, emulateConfig)
    }

    private fun getFilePath(): FlipperFilePath {
        info { "Try get file path" }
        val filePathString =
            inputData.getString(EXTRA_KEY_FILE_PATH) ?: error("Not found file path")

        val filePath = File(filePathString)
        val folder =
            filePath.parent ?: FlipperKeyType.getByExtension(filePath.extension)?.flipperDir.orEmpty()
        return FlipperFilePath(
            folder,
            filePath.name
        )
    }

    override suspend fun getForegroundInfo() = widgetNotificationHelper.startForegroundInfo(id)

    private suspend fun isSynced(filePath: FlipperFilePath): Boolean {
        val keyPath = FlipperKeyPath(filePath, deleted = false)
        val flipperKey = simpleKeyApi.getKey(keyPath)
        if (flipperKey == null) {
            info { "$filePath not found" }
            return false
        }
        return flipperKey.synchronized
    }
}
