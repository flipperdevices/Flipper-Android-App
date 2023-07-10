package com.flipperdevices.faphub.installedtab.impl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installedtab.impl.model.FapBatchUpdateButtonState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import com.flipperdevices.faphub.installedtab.impl.model.OfflineFapApp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tangle.viewmodel.VMInject

class InstalledFapsViewModel @VMInject constructor(
    private val fapManifestApi: FapManifestApi,
    private val queueApi: FapInstallationQueueApi,
    private val fapsStateProducer: InstalledFapsFromNetworkProducer
) : ViewModel(), LogTagProvider {
    override val TAG = "InstalledFapsViewModel"
    private var fetcherJob: Job? = null

    private val installedFapsStateFlow = MutableStateFlow<FapInstalledInternalLoadingState>(
        FapInstalledInternalLoadingState.Loading
    )

    init {
        refresh(force = false)
    }

    fun getFapInstalledScreenState(): StateFlow<FapInstalledScreenState> =
        installedFapsStateFlow.map {
            when (it) {
                is FapInstalledInternalLoadingState.Error -> FapInstalledScreenState.Error(it.throwable)
                FapInstalledInternalLoadingState.Loading -> FapInstalledScreenState.Loading
                is FapInstalledInternalLoadingState.Loaded -> FapInstalledScreenState.Loaded(
                    it.faps.sortedWith(
                        compareBy(
                            { (_, fapState) -> fapState },
                            { (fapItem, _) -> fapItem.name }
                        )
                    ).toImmutableList()
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

    fun updateAll() = viewModelScope.launch(Dispatchers.Default) {
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
                queueApi.enqueueSync(
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

    fun cancelAll() = viewModelScope.launch(Dispatchers.Default) {
        val state = installedFapsStateFlow.first()
        if (state !is FapInstalledInternalLoadingState.Loaded) {
            info { "State is $state, so just return" }
            return@launch
        }
        state.faps.filter { (_, state) -> state is FapInstalledInternalState.UpdatingInProgress }
            .forEach { (fapItem, _) ->
                queueApi.enqueueSync(FapActionRequest.Cancel(fapItem.id))
            }
    }

    fun refresh(force: Boolean) {
        fapsStateProducer.refresh(viewModelScope, force)
        val oldJob = fetcherJob
        fetcherJob = viewModelScope.launch(Dispatchers.Default) {
            oldJob?.cancelAndJoin()
            installedFapsStateFlow.emit(FapInstalledInternalLoadingState.Loading)
            fapManifestApi.invalidateAsync()

            fapsStateProducer.getLoadedFapsFlow().catch {
                if (it is CancellationException) {
                    return@catch
                }
                error(it) { "Failed get installed items" }
                installedFapsStateFlow.emit(FapInstalledInternalLoadingState.Error(it))
            }.collectLatest {
                installedFapsStateFlow.emit(it)
            }
        }
    }
}

sealed class FapInstalledInternalLoadingState {
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
