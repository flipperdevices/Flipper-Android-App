package com.flipperdevices.updater.impl.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json.Default.decodeFromString
import javax.inject.Inject

class UpdaterWorkManager(
    val context: Context,
    val params: WorkerParameters
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
        val updateRequestJson = inputData.getString(UPDATE_REQUEST_KEY) ?: return Result.failure()
        verbose { "Update request in string: $updateRequestJson" }

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

        localActiveTask.start(updateRequest) {
            info { "Updater state update to $it" }

            withContext(NonCancellable) {
                updaterStateHolder.updateState(UpdatingStateWithRequest(it, request = updateRequest))

                val endReason: UpdateStatus? = when (it) {
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

                if (it.isFinalState) {
                    info { "Updater state is final, stopping" }
                    currentActiveTask?.onStop()
                    currentActiveTask = null
                    return@withContext
                }
            }
        }

        info { "Updater task returned" }
        return Result.success()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return UpdaterNotification.getForegroundInfo(this.id, context)
    }

    suspend fun cancel(silent: Boolean) {
        val updateRequest = updaterStateHolder.getState().value.request
        if (updateRequest != null && !silent) {
            metricApi.reportComplexEvent(
                UpdateFlipperEnd(
                    updateFrom = updateRequest.updateFrom.version,
                    updateTo = updateRequest.updateTo.version,
                    updateId = updateRequest.requestId,
                    updateStatus = UpdateStatus.CANCELED
                )
            )
        }
        currentActiveTask?.onStop()
    }
}
