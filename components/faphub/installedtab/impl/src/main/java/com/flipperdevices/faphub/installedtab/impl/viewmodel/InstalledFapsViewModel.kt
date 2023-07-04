package com.flipperdevices.faphub.installedtab.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.model.FapManifestEnrichedItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.installedtab.impl.model.FapBatchUpdateButtonState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import com.flipperdevices.faphub.installedtab.impl.model.OfflineFapApp
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.faphub.target.model.FlipperTarget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class InstalledFapsViewModel @VMInject constructor(
    private val fapNetworkApi: FapNetworkApi,
    private val fapManifestApi: FapManifestApi,
    private val fapStateManager: FapInstallationStateManager,
    private val queueApi: FapInstallationQueueApi,
    private val targetProviderApi: FlipperTargetProviderApi
) : ViewModel(), LogTagProvider {
    override val TAG = "InstalledFapsViewModel"
    private var fetcherJob: Job? = null
    private val installedFapsStateFlow = MutableStateFlow<FapInstalledInternalLoadingState>(
        FapInstalledInternalLoadingState.Loading
    )

    init {
        refresh()
    }

    fun getFapInstalledScreenState(): StateFlow<FapInstalledScreenState> =
        installedFapsStateFlow.map {
            when (it) {
                is FapInstalledInternalLoadingState.Error -> FapInstalledScreenState.Error(it.throwable)
                FapInstalledInternalLoadingState.Loading -> FapInstalledScreenState.Loading
                is FapInstalledInternalLoadingState.Loaded -> FapInstalledScreenState.Loaded(
                    it.faps.sortedByDescending { (_, fapState) -> fapState }
                        .map { (fapItem, _) -> fapItem }.toImmutableList()
                )

                is FapInstalledInternalLoadingState.LoadedOffline -> FapInstalledScreenState.LoadedOffline(
                    it.faps
                )
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, FapInstalledScreenState.Loading)

    fun getFapBatchUpdateButtonState(): StateFlow<FapBatchUpdateButtonState> =
        installedFapsStateFlow.map { state ->
            when (state) {
                is FapInstalledInternalLoadingState.Error -> FapBatchUpdateButtonState.NoUpdates
                FapInstalledInternalLoadingState.Loading -> FapBatchUpdateButtonState.Loading
                is FapInstalledInternalLoadingState.LoadedOffline -> FapBatchUpdateButtonState.Offline
                is FapInstalledInternalLoadingState.Loaded -> {
                    val updatingInProgress = state.faps.count {
                        it.second is FapInstalledInternalState.UpdatingInProgress
                    }
                    val pendingToUpdate = state.faps.count {
                        it.second is FapInstalledInternalState.ReadyToUpdate
                    }
                    if (pendingToUpdate > 0) {
                        FapBatchUpdateButtonState.ReadyToUpdate(pendingToUpdate)
                    } else if (updatingInProgress > 0) {
                        FapBatchUpdateButtonState.UpdatingInProgress
                    } else {
                        FapBatchUpdateButtonState.NoUpdates
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Eagerly, FapBatchUpdateButtonState.Loading)

    fun updateAll() = viewModelScope.launch {
        val state = installedFapsStateFlow.first()
        if (state !is FapInstalledInternalLoadingState.Loaded) {
            info { "State is $state, so just return" }
            return@launch
        }
        val toUpdate = state.faps.filter { (_, state) ->
            state is FapInstalledInternalState.ReadyToUpdate
        }
        info { "Pending items is $toUpdate" }

        toUpdate.forEach { (fapItem, state) ->
            if (state is FapInstalledInternalState.ReadyToUpdate) {
                queueApi.enqueue(
                    FapActionRequest.Update(
                        from = state.manifestItem,
                        toVersion = fapItem.upToDateVersion,
                        iconUrl = fapItem.picUrl,
                        applicationName = fapItem.name
                    )
                )
            }
        }
    }

    fun cancelAll() = viewModelScope.launch {
        val state = installedFapsStateFlow.first()
        if (state !is FapInstalledInternalLoadingState.Loaded) {
            info { "State is $state, so just return" }
            return@launch
        }
        state.faps.filter { (_, state) -> state is FapInstalledInternalState.UpdatingInProgress }
            .forEach { (fapItem, _) ->
                queueApi.enqueue(FapActionRequest.Cancel(fapItem.id))
            }
    }

    fun refresh() {
        val oldJob = fetcherJob
        fetcherJob = viewModelScope.launch {
            oldJob?.cancelAndJoin()
            installedFapsStateFlow.emit(FapInstalledInternalLoadingState.Loading)
            fapManifestApi.invalidateAsync()
            combine(
                fapManifestApi.getManifestFlow(),
                targetProviderApi.getFlipperTarget().filterNotNull()
            ) { manifestState, flipperTarget ->
                manifestState to flipperTarget
            }.flatMapLatest { (manifestState, flipperTarget) ->
                when (manifestState) {
                    is FapManifestState.LoadedOffline -> flowOf(
                        FapInstalledInternalLoadingState.LoadedOffline(
                            manifestState.items.map { OfflineFapApp(it) }.toImmutableList()
                        )
                    )

                    FapManifestState.Loading -> flowOf(FapInstalledInternalLoadingState.Loading)
                    is FapManifestState.NotLoaded -> flowOf(
                        FapInstalledInternalLoadingState.Error(
                            manifestState.throwable
                        )
                    )

                    is FapManifestState.Loaded -> getLoadedFapsState(
                        manifestState.items,
                        flipperTarget
                    )
                }
            }.catch {
                if (it is CancellationException) {
                    return@catch
                }
                error(it) { "Failed get installed items" }
                installedFapsStateFlow.emit(FapInstalledInternalLoadingState.Error(it))
            }.collect {
                installedFapsStateFlow.emit(it)
            }
        }
    }

    private suspend fun getLoadedFapsState(
        manifestItems: List<FapManifestEnrichedItem>,
        flipperTarget: FlipperTarget
    ): Flow<FapInstalledInternalLoadingState> {
        val faps = fapNetworkApi.getAllItem(
            applicationIds = manifestItems.map { it.fapManifestItem.uid },
            offset = 0,
            limit = manifestItems.size,
            sortType = SortType.UPDATE_AT_DESC,
            target = flipperTarget
        ).getOrThrow().associateBy { it.id }
        val flows = manifestItems.mapNotNull { manifestItem ->
            faps[manifestItem.fapManifestItem.uid]?.let { manifestItem to it }
        }.map { (_, fapItem) ->
            fapStateManager.getFapStateFlow(
                applicationUid = fapItem.id,
                currentVersion = fapItem.upToDateVersion
            ).map { Pair(fapItem, it) }
        }
        return combine(flows) { items ->
            items.map { (fapItem, state) ->
                fapItem to when (state) {
                    is FapState.ReadyToUpdate -> FapInstalledInternalState.ReadyToUpdate(state.from)
                    is FapState.UpdatingInProgress -> FapInstalledInternalState.UpdatingInProgress
                    else -> FapInstalledInternalState.Installed
                }
            }
        }.map { FapInstalledInternalLoadingState.Loaded(it.toImmutableList()) }
    }
}

private sealed class FapInstalledInternalLoadingState {
    object Loading : FapInstalledInternalLoadingState()

    data class LoadedOffline(
        val faps: ImmutableList<OfflineFapApp>
    ) : FapInstalledInternalLoadingState()

    data class Loaded(
        val faps: ImmutableList<Pair<FapItemShort, FapInstalledInternalState>>
    ) : FapInstalledInternalLoadingState()

    data class Error(
        val throwable: Throwable
    ) : FapInstalledInternalLoadingState()
}

private sealed class FapInstalledInternalState : Comparable<FapInstalledInternalState> {
    object UpdatingInProgress : FapInstalledInternalState()

    class ReadyToUpdate(
        val manifestItem: FapManifestItem
    ) : FapInstalledInternalState()

    object Installed : FapInstalledInternalState()

    @Suppress("MagicNumber")
    override fun compareTo(other: FapInstalledInternalState): Int {
        return when (this) {
            Installed -> when (other) {
                Installed -> 0
                is ReadyToUpdate -> -1
                UpdatingInProgress -> -2
            }

            is ReadyToUpdate -> when (other) {
                Installed -> +1
                is ReadyToUpdate -> 0
                UpdatingInProgress -> -1
            }

            UpdatingInProgress -> when (other) {
                Installed -> +2
                is ReadyToUpdate -> +1
                UpdatingInProgress -> 0
            }
        }
    }
}
