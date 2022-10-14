package com.flipperdevices.widget.impl.tasks

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.withCoroutineScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.keyscreen.api.EmulateHelper
import com.flipperdevices.widget.impl.di.WidgetComponent
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.invalidate.WidgetNotificationHelper
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

const val EXTRA_KEY_FILE_PATH = "file_path"
const val EXTRA_KEY_WIDGET_ID = AppWidgetManager.EXTRA_APPWIDGET_ID
private const val DEFAULT_WIDGET_APP_ID = -1

class StartEmulateWorker(
    context: Context, params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG = "StartEmulateWorker"

    @Inject
    lateinit var serviceApiProvider: FlipperServiceProvider

    @Inject
    lateinit var emulateHelper: EmulateHelper

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
        // Start emulate
        setForegroundAsync(widgetNotificationHelper.startForegroundInfo(id))
        val serviceApi = serviceApiProvider.getServiceApi()
        try {
            startEmulate(scope, serviceApi.requestApi)
        } catch (throwable: Throwable) {
            error(throwable) { "Failed emulate $inputData" }
            widgetStateStorage.updateState(widgetId, WidgetState.ERROR)
            return@withCoroutineScope Result.failure()
        }
        widgetStateStorage.updateState(widgetId, WidgetState.IN_PROGRESS)

        return@withCoroutineScope Result.success()
    }

    private suspend fun startEmulate(
        scope: CoroutineScope,
        requestApi: FlipperRequestApi
    ) {
        info { "Start emulate" }
        val filePath = getFilePath()
        val keyType = filePath.keyType ?: error("Not found key type")
        emulateHelper.startEmulate(scope, requestApi, keyType, filePath)
    }

    private fun getFilePath(): FlipperFilePath {
        info { "Try get file path" }
        val filePathString =
            inputData.getString(EXTRA_KEY_FILE_PATH) ?: error("Not found file path")

        val filePath = File(filePathString)
        val folder =
            filePath.parent ?: FlipperKeyType.getByExtension(filePath.extension)?.flipperDir ?: ""
        return FlipperFilePath(
            folder, filePath.name
        )
    }
}