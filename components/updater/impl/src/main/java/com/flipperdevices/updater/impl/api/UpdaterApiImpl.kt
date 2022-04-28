package com.flipperdevices.updater.impl.api

import android.content.Context
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.updater.api.DownloaderApi
import com.flipperdevices.updater.api.UpdaterApi
import com.flipperdevices.updater.impl.UpdaterTask
import com.flipperdevices.updater.model.UpdatingState
import com.flipperdevices.updater.model.UpdatingStateWithVersion
import com.flipperdevices.updater.model.VersionFiles
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
    private val downloaderApi: DownloaderApi
) : UpdaterApi, LogTagProvider {
    override val TAG = "UpdaterApi"

    private val updatingState = MutableStateFlow<UpdatingStateWithVersion>(
        UpdatingStateWithVersion(UpdatingState.NotStarted, version = null)
    )

    private var currentActiveTask: UpdaterTask? = null
    private val isLaunched = AtomicBoolean(false)

    override fun start(versionFiles: VersionFiles) {
        info { "Request update with file $versionFiles" }
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

        localActiveTask.start(versionFiles.updaterFile) {
            info { "Downloading state update to $it" }
            withContext(NonCancellable) {
                updatingState.emit(UpdatingStateWithVersion(it, version = versionFiles.version))

                if (it == UpdatingState.NotStarted || it == UpdatingState.Rebooting) {
                    currentActiveTask?.onStop()
                    currentActiveTask = null
                    isLaunched.set(false)
                }
            }
        }
    }

    override suspend fun cancel() {
        currentActiveTask?.onStop()
    }

    override fun onDeviceConnected() {
        updatingState.update {
            if (it.state == UpdatingState.Rebooting) {
                UpdatingStateWithVersion(UpdatingState.NotStarted, version = it.version)
            } else it
        }
    }

    override fun getState(): StateFlow<UpdatingStateWithVersion> = updatingState
    override fun isUpdateInProcess(): Boolean {
        return isLaunched.get()
    }
}
