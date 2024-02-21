package com.flipperdevices.faphub.installedtab.impl.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.flipperdevices.faphub.installedtab.impl.model.OfflineFapApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class InstalledFapsUidsProducer @Inject constructor(
    private val queueApi: FapInstallationQueueApi,
    private val fapManifestApi: FapManifestApi
) : LogTagProvider {
    override val TAG = "InstalledFapsStateProducer"

    private val applicationUidsStateFlow = MutableStateFlow<FapInstalledUidsState>(
        FapInstalledUidsState.Loading
    )
    private var applicationUidJob: Job? = null

    internal fun getUidsStateFlow() = applicationUidsStateFlow.asStateFlow()

    fun refresh(scope: CoroutineScope, force: Boolean) {
        if (force) {
            fapManifestApi.invalidateAsync()
        }
        val oldJob = applicationUidJob
        applicationUidJob = scope.launch(Dispatchers.Default) {
            oldJob?.cancelAndJoin()
            if (force) {
                applicationUidsStateFlow.emit(FapInstalledUidsState.Loading)
            }
            combine(
                fapManifestApi.getManifestFlow(),
                queueApi.getAllTasks()
            ) { fapManifestState, fapQueueStates ->
                fapManifestState to fapQueueStates
            }.collectLatest { (manifestState, tasks) ->
                val state = when (manifestState) {
                    is FapManifestState.LoadedOffline -> FapInstalledUidsState.LoadedOffline(
                        manifestState.items.map { OfflineFapApp(it) }.toImmutableList()
                    )

                    FapManifestState.Loading -> FapInstalledUidsState.Loading
                    is FapManifestState.NotLoaded -> FapInstalledUidsState.Error(
                        manifestState.throwable
                    )

                    is FapManifestState.Loaded -> {
                        val ids = tasks.mapNotNull {
                            when (it) {
                                is FapQueueState.Failed,
                                FapQueueState.NotFound -> null

                                is FapQueueState.InProgress -> it.request.applicationUid
                                is FapQueueState.Pending -> it.request.applicationUid
                            }
                        } + manifestState.items.map { it.fapManifestItem.uid }
                        FapInstalledUidsState.Loaded(
                            ids.distinct().sorted().toImmutableList()
                        )
                    }
                }
                applicationUidsStateFlow.emit(state)
            }
        }
    }
}

internal sealed class FapInstalledUidsState {
    data object Loading : FapInstalledUidsState()

    data class LoadedOffline(
        val faps: ImmutableList<OfflineFapApp>
    ) : FapInstalledUidsState()

    data class Loaded(
        val faps: ImmutableList<String>
    ) : FapInstalledUidsState()

    data class Error(
        val throwable: Throwable
    ) : FapInstalledUidsState()
}
