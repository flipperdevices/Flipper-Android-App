package com.flipperdevices.updater.impl.api

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.impl.service.UpdaterWorkManager
import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.FirmwareVersion
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.model.UpdatingStateWithRequest
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, UpdaterApi::class)
class UpdaterApiImpl @Inject constructor(
    private val context: Context,
    private val updaterStateHolder: UpdaterStateHolder
) : UpdaterApi, LogTagProvider {
    override val TAG = "UpdaterFlipperApi"

    private var workerId: UUID? = null
    private val isLaunched = AtomicBoolean(false)

    private val workManager by lazy { WorkManager.getInstance(context) }

    override fun start(updateRequest: UpdateRequest) {
        info { "Request update with file $updateRequest" }
        if (!isLaunched.compareAndSet(false, true)) {
            info { "Update skipped, because we already in update" }
            return
        }

        val updateWorker = OneTimeWorkRequestBuilder<UpdaterWorkManager>()
            .setInputData(
                Data.Builder()
                    .putString(
                        UpdaterWorkManager.UPDATE_REQUEST_KEY,
                        updateRequest.encode()
                    )
                    .build()
            )
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        workManager.enqueue(updateWorker)
        workerId = updateWorker.id
        info { "Update Worker id $workerId" }

        val workInfo = workManager.getWorkInfoById(updateWorker.id).get()
        info { "Work Manager End: ${workInfo.id} ${workInfo.state}" }
        isLaunched.set(false)
    }

    override suspend fun cancel(silent: Boolean) {
        workManager.cancelWorkById(workerId ?: return)
        isLaunched.set(false)
    }

    override fun onDeviceConnected(versionName: FirmwareVersion) {
        updaterStateHolder.update {
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

    override fun getState(): StateFlow<UpdatingStateWithRequest> = updaterStateHolder.getState()
    override fun resetState() {
        isLaunched.compareAndSet(true, false)
        updaterStateHolder.update {
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
