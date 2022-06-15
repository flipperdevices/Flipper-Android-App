package com.flipperdevices.updater.impl.api

import android.content.Context
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.toIntSafe
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.complex.UpdateFlipperEnd
import com.flipperdevices.metric.api.events.complex.UpdateFlipperStart
import com.flipperdevices.metric.api.events.complex.UpdateStatus
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.impl.UpdaterTask
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.model.UpdatingStateWithRequest
import com.squareup.anvil.annotations.ContributesBinding
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

@Singleton
@ContributesBinding(AppGraph::class, UpdaterApi::class)
class UpdaterApiImpl @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val context: Context,
    private val downloaderApi: DownloaderApi,
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
            downloaderApi,
            context
        )
        currentActiveTask = localActiveTask

        metricApi.reportComplexEvent(
            UpdateFlipperStart(
                updateFromVersion = updateRequest.updateFrom.version,
                updateToVersion = updateRequest.updateTo.version.version,
                updateId = updateRequest.requestId.toIntSafe()
            )
        )

        localActiveTask.start(updateRequest) {
            info { "Downloading state update to $it" }
            withContext(NonCancellable) {
                updatingState.emit(UpdatingStateWithRequest(it, request = updateRequest))

                if (it == UpdatingState.NotStarted || it == UpdatingState.Rebooting) {
                    currentActiveTask?.onStop()
                    currentActiveTask = null
                    isLaunched.set(false)
                }
            }
        }
    }

    override suspend fun cancel() {
        val updateRequest = updatingState.value.request
        if (updateRequest != null) {
            metricApi.reportComplexEvent(
                UpdateFlipperEnd(
                    updateFrom = updateRequest.updateFrom.version,
                    updateTo = updateRequest.updateTo.version.version,
                    updateId = updateRequest.requestId.toIntSafe(),
                    updateStatus = UpdateStatus.CANCELED
                )
            )
        }
        currentActiveTask?.onStop()
    }

    override fun onDeviceConnected() {
        updatingState.update {
            if (it.state == UpdatingState.Rebooting) {
                UpdatingStateWithRequest(UpdatingState.NotStarted, request = it.request)
            } else it
        }
    }

    override fun getState(): StateFlow<UpdatingStateWithRequest> = updatingState
    override fun isUpdateInProcess(): Boolean {
        return isLaunched.get()
    }
}
