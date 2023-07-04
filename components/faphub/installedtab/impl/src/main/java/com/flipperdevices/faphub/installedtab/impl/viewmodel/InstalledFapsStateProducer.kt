package com.flipperdevices.faphub.installedtab.impl.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.installedtab.impl.model.OfflineFapApp
import com.flipperdevices.faphub.target.model.FlipperTarget
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class InstalledFapsStateProducer @Inject constructor(
    private val queueApi: FapInstallationQueueApi,
    private val fapNetworkApi: FapNetworkApi,
    private val fapStateManager: FapInstallationStateManager,
    private val fapManifestApi: FapManifestApi
) : LogTagProvider {
    override val TAG = "InstalledFapsStateProducer"

    private val applicationUidsStateFlow = MutableStateFlow<FapInstalledInternalCombinedState>(
        FapInstalledInternalCombinedState.Loading
    )
    private var applicationUidJob: Job? = null

    fun refresh(scope: CoroutineScope) {
        val oldJob = applicationUidJob
        applicationUidJob = scope.launch {
            oldJob?.cancelAndJoin()
            combine(
                fapManifestApi.getManifestFlow(),
                queueApi.getAllTasks()
            ) { fapManifestState, fapQueueStates ->
                fapManifestState to fapQueueStates
            }.collectLatest { (manifestState, tasks) ->
                val state = when (manifestState) {
                    is FapManifestState.LoadedOffline -> FapInstalledInternalCombinedState.LoadedOffline(
                        manifestState.items.map { OfflineFapApp(it) }.toImmutableList()
                    )

                    FapManifestState.Loading -> FapInstalledInternalCombinedState.Loading
                    is FapManifestState.NotLoaded -> FapInstalledInternalCombinedState.Error(
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
                        FapInstalledInternalCombinedState.Loaded(
                            ids.distinct().sorted().toImmutableList()
                        )
                    }
                }
                applicationUidsStateFlow.emit(state)
            }
        }
    }

    suspend fun getLoadedFapsFlow(
        target: FlipperTarget
    ): Flow<FapInstalledInternalLoadingState> {
        return applicationUidsStateFlow.flatMapLatest { state ->
            return@flatMapLatest when (state) {
                is FapInstalledInternalCombinedState.Error -> flowOf(
                    FapInstalledInternalLoadingState.Error(
                        state.throwable
                    )
                )

                is FapInstalledInternalCombinedState.LoadedOffline -> flowOf(
                    FapInstalledInternalLoadingState.LoadedOffline(
                        state.faps
                    )
                )

                FapInstalledInternalCombinedState.Loading -> flowOf(FapInstalledInternalLoadingState.Loading)

                is FapInstalledInternalCombinedState.Loaded -> getLoadedFapsFlow(
                    toRequestItems = state.faps,
                    flipperTarget = target
                )
            }
        }
    }

    private suspend fun getLoadedFapsFlow(
        toRequestItems: ImmutableList<String>,
        flipperTarget: FlipperTarget
    ): Flow<FapInstalledInternalLoadingState.Loaded> {
        info { "Update #getLoadedFapsState. Ids: $toRequestItems" }
        if (toRequestItems.isEmpty()) {
            return flowOf(FapInstalledInternalLoadingState.Loaded(persistentListOf()))
        }
        val faps = fapNetworkApi.getAllItem(
            applicationIds = toRequestItems,
            offset = 0,
            limit = toRequestItems.size,
            sortType = SortType.UPDATE_AT_DESC,
            target = flipperTarget
        ).getOrThrow().associateBy { it.id }
        val flows = toRequestItems.mapNotNull { applicationUid ->
            faps[applicationUid]
        }.map { fapItem ->
            fapStateManager.getFapStateFlow(
                applicationUid = fapItem.id,
                currentVersion = fapItem.upToDateVersion
            ).map { Pair(fapItem, it) }
        }
        return combine(flows) { items ->
            items.map { (fapItem, state) ->
                fapItem to when (state) {
                    is FapState.ReadyToUpdate -> FapInstalledInternalState.ReadyToUpdate(state.from)
                    is FapState.InstallationInProgress -> if (state.active) {
                        FapInstalledInternalState.InstallingInProgressActive
                    } else {
                        FapInstalledInternalState.InstallingInProgress
                    }

                    is FapState.UpdatingInProgress -> if (state.active) {
                        FapInstalledInternalState.UpdatingInProgressActive
                    } else {
                        FapInstalledInternalState.UpdatingInProgress
                    }

                    else -> FapInstalledInternalState.Installed
                }
            }
        }.map { FapInstalledInternalLoadingState.Loaded(it.toImmutableList()) }
    }
}

private sealed class FapInstalledInternalCombinedState {
    object Loading : FapInstalledInternalCombinedState()

    data class LoadedOffline(
        val faps: ImmutableList<OfflineFapApp>
    ) : FapInstalledInternalCombinedState()

    data class Loaded(
        val faps: ImmutableList<String>
    ) : FapInstalledInternalCombinedState()

    data class Error(
        val throwable: Throwable
    ) : FapInstalledInternalCombinedState()
}
