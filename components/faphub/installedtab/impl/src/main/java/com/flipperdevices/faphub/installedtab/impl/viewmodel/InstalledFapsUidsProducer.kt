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
import kotlinx.collections.immutable.persistentListOf

class InstalledFapsUidsProducer @Inject constructor(
    private val queueApi: FapInstallationQueueApi,
    private val fapManifestApi: FapManifestApi
) : LogTagProvider {
    override val TAG = "InstalledFapsStateProducer"

    private val applicationUidsStateFlow = MutableStateFlow<FapInstalledUidsState>(
        FapInstalledUidsState.Loaded(
            faps = persistentListOf(),
            inProgress = true
        )
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
                applicationUidsStateFlow.emit(
                    FapInstalledUidsState.Loaded(
                        faps = persistentListOf(),
                        inProgress = true
                    )
                )
            }
            combine(
                fapManifestApi.getManifestFlow(),
                queueApi.getAllTasks()
            ) { fapManifestState, fapQueueStates ->
                fapManifestState to fapQueueStates
            }.collectLatest { (manifestState, tasks) ->
                val state = when (manifestState) {
                    is FapManifestState.NotLoaded -> FapInstalledUidsState.Error(
                        manifestState.throwable
                    )

                    is FapManifestState.Loaded -> {
                        val ids = tasks.asSequence().mapNotNull {
                            when (it) {
                                is FapQueueState.Failed,
                                FapQueueState.NotFound -> null

                                is FapQueueState.InProgress -> it.request.applicationUid
                                is FapQueueState.Pending -> it.request.applicationUid
                            }
                        }.map { FapInstalled.RawUid(applicationUid = it) }
                            .plus(manifestState.items.map { FapInstalled.Offline(OfflineFapApp(it)) })
                            .distinctBy { it.applicationUid }
                            .sortedBy { it.applicationUid }.toList()
                            .toImmutableList()

                        FapInstalledUidsState.Loaded(
                            faps = ids,
                            inProgress = manifestState.inProgress
                        )
                    }
                }
                applicationUidsStateFlow.emit(state)
            }
        }
    }
}

internal sealed class FapInstalled {
    abstract val applicationUid: String

    data class RawUid(
        override val applicationUid: String
    ) : FapInstalled()

    data class Offline(
        val offlineFap: OfflineFapApp
    ) : FapInstalled() {
        override val applicationUid = offlineFap.applicationUid
    }
}

internal sealed class FapInstalledUidsState {
    data class Loaded(
        val faps: ImmutableList<FapInstalled>,
        val inProgress: Boolean
    ) : FapInstalledUidsState()

    data class Error(
        val throwable: Throwable
    ) : FapInstalledUidsState()
}
