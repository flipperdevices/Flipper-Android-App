package com.flipperdevices.bridge.synchronization.impl.api

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.dao.api.delegates.FavoriteApi
import com.flipperdevices.bridge.dao.api.delegates.key.DeleteKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.SimpleKeyApi
import com.flipperdevices.bridge.dao.api.delegates.key.UtilsKeyApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.bridge.synchronization.api.SynchronizationApi
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.impl.SynchronizationTask
import com.flipperdevices.bridge.synchronization.impl.utils.SynchronizationPercentProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.metric.api.MetricApi
import com.squareup.anvil.annotations.ContributesBinding
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Singleton
@ContributesBinding(AppGraph::class, SynchronizationApi::class)
@Suppress("LongParameterList")
class SynchronizationApiImpl @Inject constructor(
    private val serviceProvider: FlipperServiceProvider,
    private val simpleKeyApi: SimpleKeyApi,
    private val deleteKeyApi: DeleteKeyApi,
    private val utilsKeyApi: UtilsKeyApi,
    private val favoriteApi: FavoriteApi,
    private val metricApi: MetricApi,
    private val preference: DataStore<Settings>
) : SynchronizationApi, LogTagProvider {
    override val TAG = "SynchronizationApi"

    private val isLaunched = AtomicBoolean(true) // TODO remove
    private val synchronizationState = MutableStateFlow<SynchronizationState>(
        SynchronizationState.NotStarted
    )
    private var markDirty = false

    private var synchronizationTask: SynchronizationTask? = null

    @Synchronized
    override fun startSynchronization(force: Boolean) {
        info { "Request synchronization..." }
        if (!isLaunched.compareAndSet(false, true)) {
            markDirty = true
            info { "Synchronization skipped, because we already in synchronization" }
            return
        }
        markDirty = false
        val localSynchronizationTask = SynchronizationTask(
            serviceProvider = serviceProvider,
            simpleKeyApi = simpleKeyApi,
            deleteKeyApi = deleteKeyApi,
            utilsKeyApi = utilsKeyApi,
            favoriteApi = favoriteApi,
            metricApi = metricApi,
            synchronizationProvider = SynchronizationPercentProvider(preference)
        )

        localSynchronizationTask.start(input = Unit) { taskState ->
            synchronizationState.update { taskState }
            if (taskState == SynchronizationState.Finished) {
                isLaunched.compareAndSet(true, false)
                if (markDirty) {
                    startSynchronization()
                }
            }
        }
        synchronizationTask = localSynchronizationTask
    }

    override suspend fun stop() {
        markDirty = false
        synchronizationTask?.onStop()
    }

    override fun isSynchronizationRunning() = isLaunched.get()

    override fun getSynchronizationState(): StateFlow<SynchronizationState> {
        return synchronizationState
    }
}
