package com.flipperdevices.updater.impl.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.UpdateFlipperStart
import com.flipperdevices.updater.impl.UpdaterTask
import com.flipperdevices.updater.impl.di.UpdaterComponent
import com.flipperdevices.updater.impl.tasks.UploadToFlipperHelper
import com.flipperdevices.updater.impl.tasks.downloader.UpdateContentDownloader
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import javax.inject.Inject

class StartUpdateWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), LogTagProvider {
    override val TAG: String = "StartUpdateWorker"

    companion object {
        const val UPDATE_REQUEST_KEY = "update_request"
    }

    init {
        ComponentHolder.component<UpdaterComponent>().inject(this)
    }

    @Inject
    private lateinit var serviceProvider: FlipperServiceProvider

    @Inject
    private lateinit var updateContentDownloader: MutableSet<UpdateContentDownloader>

    @Inject
    private lateinit var subGhzProvisioningHelper: SubGhzProvisioningHelper

    @Inject
    private lateinit var uploadToFlipperHelper: UploadToFlipperHelper

    @Inject
    private lateinit var metricApi: MetricApi

    @Inject
    private lateinit var flipperStorageApi: FlipperStorageApi

    override suspend fun doWork(): Result {
        val updateRequest = inputData.getString(UPDATE_REQUEST_KEY)?.let {
            UpdateRequest.fromSerializable(it)
        } ?: return Result.failure()

        val localActiveTask = UpdaterTask(
            serviceProvider,
            context,
            uploadToFlipperHelper,
            subGhzProvisioningHelper,
            updateContentDownloader,
            flipperStorageApi
        )

        metricApi.reportComplexEvent(
            UpdateFlipperStart(
                updateFromVersion = updateRequest.updateFrom.version,
                updateToVersion = updateRequest.updateTo.version,
                updateId = updateRequest.requestId
            )
        )
    }

    override suspend fun getForegroundInfo() = super.getForegroundInfo()
}
