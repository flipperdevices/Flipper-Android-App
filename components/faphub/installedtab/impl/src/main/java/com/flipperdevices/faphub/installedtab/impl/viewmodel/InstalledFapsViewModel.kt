package com.flipperdevices.faphub.installedtab.impl.viewmodel

import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.queue.api.FapInstallationQueueApi
import com.flipperdevices.faphub.installation.queue.api.model.FapActionRequest
import com.flipperdevices.faphub.installedtab.impl.model.FapBatchUpdateButtonState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import com.flipperdevices.faphub.installedtab.impl.model.OfflineFapApp
import com.flipperdevices.faphub.installedtab.impl.model.toButtonState
import com.flipperdevices.faphub.installedtab.impl.model.toScreenState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class InstalledFapsViewModel @Inject constructor(
    private val fapManifestApi: FapManifestApi,
    private val queueApi: FapInstallationQueueApi,
    fapsStateProducerProvider: Provider<InstalledFapsFromNetworkProducer>
) : DecomposeViewModel(), LogTagProvider {
    override val TAG = "InstalledFapsViewModel"
    private var fetcherJob: Job? = null

    private val fapsStateProducer by fapsStateProducerProvider
    private val installedFapsStateFlow = MutableStateFlow<FapInstalledInternalLoadingState>(
        FapInstalledInternalLoadingState.Loading
    )
    private val screenStateFlow = MutableStateFlow<FapInstalledScreenState>(
        FapInstalledScreenState.Loading
    )
    private val batchUpdateButtonState = MutableStateFlow<FapBatchUpdateButtonState>(
        FapBatchUpdateButtonState.Loading
    )

    init {
        refresh(force = false)
        installedFapsStateFlow.onEach {
            screenStateFlow.emit(it.toScreenState())
        }.launchIn(viewModelScope)

        installedFapsStateFlow.onEach {
            batchUpdateButtonState.emit(it.toButtonState())
        }.launchIn(viewModelScope)
    }

    fun getFapInstalledScreenState() = screenStateFlow.asStateFlow()

    fun getFapBatchUpdateButtonState() = batchUpdateButtonState.asStateFlow()

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
        fapsStateProducer.refresh(force)
        val oldJob = fetcherJob
        fetcherJob = viewModelScope.launch(Dispatchers.Default) {
            oldJob?.cancelAndJoin()
            installedFapsStateFlow.emit(FapInstalledInternalLoadingState.Loading)
            if (force) {
                fapManifestApi.invalidateAsync()
            }

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
    data object Loading : FapInstalledInternalLoadingState()

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
