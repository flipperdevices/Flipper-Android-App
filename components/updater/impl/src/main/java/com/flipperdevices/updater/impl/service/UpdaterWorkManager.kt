package com.flipperdevices.updater.impl.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.verbose
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.UpdateFlipperEnd
import com.flipperdevices.metric.api.events.complex.UpdateFlipperStart
import com.flipperdevices.metric.api.events.complex.UpdateStatus
import com.flipperdevices.updater.impl.UpdaterTask
import com.flipperdevices.updater.impl.api.UpdaterStateHolder
import com.flipperdevices.updater.impl.di.UpdaterComponent
import com.flipperdevices.updater.impl.tasks.UploadToFlipperHelper
import com.flipperdevices.updater.impl.tasks.downloader.UpdateContentDownloader
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.model.UpdatingStateWithRequest
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json.Default.decodeFromString

class UpdaterWorkManager(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG: String = "UpdaterWorkManager"

    companion object {
        const val UPDATE_REQUEST_KEY = "update_request_key"
    }

    @Inject
    lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    lateinit var uploadToFlipperHelper: UploadToFlipperHelper

    @Inject
    lateinit var subGhzProvisioningHelper: SubGhzProvisioningHelper

    @Inject
    lateinit var updateContentDownloader: MutableSet<UpdateContentDownloader>

    @Inject
    lateinit var flipperStorageApi: FlipperStorageApi

    @Inject
    lateinit var metricApi: MetricApi

    @Inject
    lateinit var updaterStateHolder: UpdaterStateHolder

    init {
        ComponentHolder.component<UpdaterComponent>().inject(this)
    }

    private var currentActiveTask: UpdaterTask? = null

    override suspend fun doWork(): Result {
        return try {
            doWorkUnSafe()
            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }
    }

    private fun doWorkUnSafe() {
        val updateRequestJson = inputData.getString(UPDATE_REQUEST_KEY)
            ?: throw Exception("Not exist data by $UPDATE_REQUEST_KEY")
        verbose { "Update request in string: $updateRequestJson" }

        // setForeground(getForegroundInfo())

        val updateRequest: UpdateRequest = decodeFromString(updateRequestJson)

        val localActiveTask = UpdaterTask(
            serviceProvider,
            context,
            uploadToFlipperHelper,
            subGhzProvisioningHelper,
            updateContentDownloader,
            flipperStorageApi
        )
        currentActiveTask = localActiveTask

        metricApi.reportComplexEvent(
            UpdateFlipperStart(
                updateFromVersion = updateRequest.updateFrom.version,
                updateToVersion = updateRequest.updateTo.version,
                updateId = updateRequest.requestId
            )
        )

        localActiveTask.start(updateRequest) { updateState ->
            info { "Updater state update to $updateState" }
            // createForegroundInfoSafe(updateState)

            withContext(NonCancellable) {
                updaterStateHolder.updateState(UpdatingStateWithRequest(updateState, request = updateRequest))

                val endReason: UpdateStatus? = when (updateState) {
                    UpdatingState.FailedDownload -> UpdateStatus.FAILED_DOWNLOAD
                    UpdatingState.FailedPrepare -> UpdateStatus.FAILED_PREPARE
                    UpdatingState.FailedUpload -> UpdateStatus.FAILED_UPLOAD
                    else -> null
                }
                if (endReason != null) {
                    metricApi.reportComplexEvent(
                        UpdateFlipperEnd(
                            updateFrom = updateRequest.updateFrom.version,
                            updateTo = updateRequest.updateTo.version,
                            updateId = updateRequest.requestId,
                            updateStatus = endReason
                        )
                    )
                }

                if (updateState.isFinalState) {
                    info { "Updater state is final, stopping" }
                    currentActiveTask?.onStop()
                    currentActiveTask = null
                    return@withContext
                }
            }
        }
    }
//
//    private fun createForegroundInfoSafe(state: UpdatingState) {
//        runCatching {
//            val foregroundInfo = UpdaterNotification.getForegroundStatusInfo(this.id, context, state)
//            setForeground(foregroundInfo)
//        }.onFailure {
//            error { "Error on setup foreground $it" }
//        }
//    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return UpdaterNotification.getForegroundInfo(this.id, context)
    }
}
