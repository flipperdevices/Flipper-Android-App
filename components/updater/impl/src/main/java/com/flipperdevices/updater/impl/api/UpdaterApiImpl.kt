package com.flipperdevices.updater.impl.api

import android.content.Context
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.impl.UpdaterTask
import com.flipperdevices.updater.model.DistributionFile
import com.flipperdevices.updater.model.UpdatingState
import com.squareup.anvil.annotations.ContributesBinding
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Singleton
@ContributesBinding(AppGraph::class, UpdaterApi::class)
class UpdaterApiImpl @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val context: Context,
    private val downloaderApi: DownloaderApi
) : UpdaterApi, LogTagProvider {
    override val TAG = "UpdaterApi"

    private val updatingState = MutableStateFlow<UpdatingState>(UpdatingState.NotStarted)

    private var currentActiveTask: UpdaterTask? = null
    private val isLaunched = AtomicBoolean(false)

    override fun start(updateFile: DistributionFile) {
        info { "Request update with file $updateFile" }
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

        localActiveTask.start(updateFile) {
            info { "Downloading state update to $it" }
            updatingState.emit(it)
            if (it == UpdatingState.NotStarted) {
                currentActiveTask?.onStop()
                currentActiveTask = null
                isLaunched.set(false)
            }
        }
    }

    override suspend fun cancel() {
        currentActiveTask?.onStop()
    }

    override fun getState(): StateFlow<UpdatingState> = updatingState
    override fun isUpdateInProcess() = isLaunched.get()
}
