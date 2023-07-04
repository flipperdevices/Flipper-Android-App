package com.flipperdevices.faphub.installedtab.impl.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.installation.manifest.model.FapManifestEnrichedItem
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapQueueState
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.target.model.FlipperTarget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class InstalledFapsStateProducer(
    scope: CoroutineScope,
    private val queueApi: FapInstallationQueueApi,
    private val fapNetworkApi: FapNetworkApi,
    private val fapStateManager: FapInstallationStateManager
) : LogTagProvider {
    override val TAG = "InstalledFapsStateProducer"

    private val tasksApplicationUidsStateFlow = MutableStateFlow<ImmutableList<String>>(
        persistentListOf()
    )

    init {
        scope.launch {
            queueApi.getAllTasks().collectLatest { tasks ->
                val ids = tasks.mapNotNull {
                    when (it) {
                        is FapQueueState.Failed,
                        FapQueueState.NotFound -> null

                        is FapQueueState.InProgress -> it.request.applicationUid
                        is FapQueueState.Pending -> it.request.applicationUid
                    }
                }
                tasksApplicationUidsStateFlow.emit(ids.toImmutableList())
            }
        }
    }

    suspend fun getLoadedFapsFlow(
        manifestItems: ImmutableList<FapManifestEnrichedItem>,
        target: FlipperTarget
    ): Flow<FapInstalledInternalLoadingState> {
        return tasksApplicationUidsStateFlow.flatMapLatest {
            getLoadedFapsFlow(it + manifestItems.map { it.fapManifestItem.uid }, target)
        }
    }

    private suspend fun getLoadedFapsFlow(
        uids: List<String>,
        flipperTarget: FlipperTarget
    ): Flow<FapInstalledInternalLoadingState> {
        info { "Update #getLoadedFapsState. Ids: $uids" }
        val toRequestItems = uids.distinct()
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
