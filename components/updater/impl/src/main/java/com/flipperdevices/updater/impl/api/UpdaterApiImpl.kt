package com.flipperdevices.updater.impl.api

import android.content.Context
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.UpdateFlipperEnd
import com.flipperdevices.metric.api.events.complex.UpdateFlipperStart
import com.flipperdevices.metric.api.events.complex.UpdateStatus
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.impl.UpdaterTask
import com.flipperdevices.updater.impl.tasks.UpdateContentHelper
import com.flipperdevices.updater.impl.tasks.UploadToFlipperHelper
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.model.UpdatingStateWithRequest
import com.flipperdevices.updater.subghz.helpers.SubGhzProvisioningHelper
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, UpdaterApi::class)
class UpdaterApiImpl @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val updateContentHelper: MutableSet<UpdateContentHelper>,
    private val subGhzProvisioningHelper: SubGhzProvisioningHelper,
    private val uploadToFlipperHelper: UploadToFlipperHelper,
    private val context: Context,
    private val metricApi: MetricApi
) : UpdaterApi, LogTagProvider {
    override val TAG = "UpdaterApi"

    private val updatingState = MutableStateFlow<UpdatingStateWithRequest>(
        UpdatingStateWithRequest(UpdatingState.NotStarted, request = null)
    )

    private var currentActiveTask: UpdaterTask? = null
    private val isLaunched = AtomicBoolean(false)

    override fun start(updateRequest: UpdateRequest) {
        info { "Request update with file $updateRequest" }
        if (!isLaunched.compareAndSet(false, true)) {
            info { "Update skipped, because we already in update" }
            return
        }
        val localActiveTask = UpdaterTask(
            serviceProvider,
            context,
            uploadToFlipperHelper,
            subGhzProvisioningHelper,
            updateContentHelper
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
                updatingState.emit(UpdatingStateWithRequest(it, request = updateRequest))

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
                    currentActiveTask?.onStop()
                    currentActiveTask = null
                    isLaunched.set(false)
                }
            }
        }
    }

    override suspend fun cancel(silent: Boolean) {
        val updateRequest = updatingState.value.request
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

    override fun onDeviceConnected(versionName: FirmwareVersion) {
        updatingState.update {
            if (it.state == UpdatingState.Rebooting) {
                if (it.request?.updateTo?.version == versionName.version) {
                    UpdatingStateWithRequest(UpdatingState.Complete, request = it.request)
                } else if (it.request?.updateTo?.channel != FirmwareChannel.CUSTOM) {
                    UpdatingStateWithRequest(UpdatingState.Failed, request = it.request)
                } else {
                    it
                }
            } else {
                it
            }
        }
    }

    override fun getState(): StateFlow<UpdatingStateWithRequest> = updatingState
    override fun resetState() {
        isLaunched.compareAndSet(true, false)
        updatingState.update {
            if (it.state != UpdatingState.NotStarted && it.state.isFinalState) {
                UpdatingStateWithRequest(UpdatingState.NotStarted, request = null)
            } else {
                it
            }
        }
    }

    override fun isUpdateInProcess(): Boolean {
        return isLaunched.get()
    }
}
