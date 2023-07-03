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
        installedFapsStateFlow.flatMapLatest { state ->
            when (state) {
                is FapInstalledInternalLoadingState.Error -> flowOf(FapBatchUpdateButtonState.NoUpdates)
                FapInstalledInternalLoadingState.Loading -> flowOf(FapBatchUpdateButtonState.Loading)
                is FapInstalledInternalLoadingState.LoadedOffline -> flowOf(
                    FapBatchUpdateButtonState.Offline
                )

                is FapInstalledInternalLoadingState.Loaded -> combine(
                    state.faps.map { it.first }.map {
                        fapStateManager.getFapStateFlow(
                            applicationUid = it.id,
                            currentVersion = it.upToDateVersion
                        )
                    }
                ) { fapStates ->
                    var updatingInProgress = 0
                    var pendingToUpdate = 0
                    fapStates.forEach {
                        when (it) {
                            FapState.Canceling,
                            FapState.Deleting,
                            is FapState.InstallationInProgress,
                            FapState.Installed,
                            FapState.NotInitialized,
                            FapState.ReadyToInstall,
                            FapState.ConnectFlipper,
                            is FapState.NotAvailableForInstall,
                            FapState.RetrievingManifest -> {
                            }

                            is FapState.UpdatingInProgress -> updatingInProgress++

                            is FapState.ReadyToUpdate -> pendingToUpdate++
                        }
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
        val toUpdate = state.faps.filter { (fapItem, _) ->
            fapStateManager.getFapStateFlow(
                applicationUid = fapItem.id,
                currentVersion = fapItem.upToDateVersion
            ).first() is FapState.ReadyToUpdate
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
        state.faps.map { it.first }.map {
            fapStateManager.getFapStateFlow(
                applicationUid = it.id,
                currentVersion = it.upToDateVersion
            ).first() to it
        }.filter { (state, _) -> state is FapState.UpdatingInProgress }
            .forEach { (_, fapItem) ->
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
                when (manifestState) {
                    is FapManifestState.LoadedOffline -> FapInstalledInternalLoadingState.LoadedOffline(
                        manifestState.items.map { OfflineFapApp(it) }.toImmutableList()
                    )

                    FapManifestState.Loading -> FapInstalledInternalLoadingState.Loading
                    is FapManifestState.NotLoaded -> FapInstalledInternalLoadingState.Error(
                        manifestState.throwable
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
                info { "Load $it" }
                installedFapsStateFlow.emit(it)
            }
        }
    }

    private suspend fun getLoadedFapsState(
        manifestItems: List<FapManifestEnrichedItem>,
        flipperTarget: FlipperTarget
    ): FapInstalledInternalLoadingState {
        val faps = fapNetworkApi.getAllItem(
            applicationIds = manifestItems.map { it.fapManifestItem.uid },
            offset = 0,
            limit = manifestItems.size,
            sortType = SortType.UPDATE_AT_DESC,
            target = flipperTarget
        ).getOrThrow().associateBy { it.id }
        val items = manifestItems.mapNotNull { manifestItem ->
            faps[manifestItem.fapManifestItem.uid]?.let { manifestItem to it }
        }.map { (manifestItem, fapItem) ->
            fapItem to if (fapItem.upToDateVersion.version > manifestItem.numberVersion
            ) {
                FapInstalledInternalState.ReadyToUpdate(
                    manifestItem = manifestItem.fapManifestItem
                )
            } else {
                FapInstalledInternalState.Installed
            }
        }
        return FapInstalledInternalLoadingState.Loaded(items.toImmutableList())
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
    class ReadyToUpdate(
        val manifestItem: FapManifestItem
    ) : FapInstalledInternalState()

    object Installed : FapInstalledInternalState()

    override fun compareTo(other: FapInstalledInternalState): Int {
        return when (this) {
            Installed -> when (other) {
                Installed -> 0
                is ReadyToUpdate -> -1
            }

            is ReadyToUpdate -> when (other) {
                Installed -> +1
                is ReadyToUpdate -> 0
            }
        }
    }
}
