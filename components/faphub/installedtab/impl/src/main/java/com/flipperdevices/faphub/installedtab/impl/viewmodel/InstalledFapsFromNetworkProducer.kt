package com.flipperdevices.faphub.installedtab.impl.viewmodel

import android.os.Looper
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.installedtab.impl.model.OfflineFapApp
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.faphub.target.model.FlipperTarget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

class InstalledFapsFromNetworkProducer @Inject constructor(
    private val installedFapsUidsProducer: InstalledFapsUidsProducer,
    private val flipperTargetProviderApi: FlipperTargetProviderApi,
    private val fapNetworkApi: FapNetworkApi,
    private val fapStateManager: FapInstallationStateManager,
) : LogTagProvider {
    override val TAG = "InstalledFapsFromNetworkProducer"

    private val applicationFromNetworkStateFlow = MutableStateFlow<FapInstalledFromNetworkState>(
        FapInstalledFromNetworkState.Loading
    )
    private var fetchFromNetworkJob: Job? = null
    private val mutex = Mutex()

    fun refresh(scope: CoroutineScope, force: Boolean) {
        installedFapsUidsProducer.refresh(scope, force)
        val oldJob = fetchFromNetworkJob
        fetchFromNetworkJob = scope.launch(Dispatchers.Default) {
            oldJob?.cancelAndJoin()
            applicationFromNetworkStateFlow.emit(FapInstalledFromNetworkState.Loading)
            combine(
                installedFapsUidsProducer.getUidsStateFlow(),
                flipperTargetProviderApi.getFlipperTarget().filterNotNull()
            ) { uidsState, flipperTarget ->
                uidsState to flipperTarget
            }.collect { (uidsState, flipperTarget) ->
                withLock(mutex, "update") {
                    val state = when (uidsState) {
                        is FapInstalledUidsState.Error -> FapInstalledFromNetworkState.Error(
                            uidsState.throwable
                        )

                        is FapInstalledUidsState.LoadedOffline -> FapInstalledFromNetworkState.LoadedOffline(
                            uidsState.faps
                        )

                        FapInstalledUidsState.Loading -> FapInstalledFromNetworkState.Loading
                        is FapInstalledUidsState.Loaded -> try {
                            updateStateUnsafe(uidsState.faps, flipperTarget)
                        } catch (throwable: Throwable) {
                            error(throwable) { "Failed load faps" }
                            FapInstalledFromNetworkState.Error(throwable)
                        }
                    }
                    applicationFromNetworkStateFlow.emit(state)
                }
            }
        }
    }

    fun getLoadedFapsFlow(): Flow<FapInstalledInternalLoadingState> {
        return applicationFromNetworkStateFlow.flatMapLatest { state ->
            return@flatMapLatest when (state) {
                is FapInstalledFromNetworkState.Error -> flowOf(
                    FapInstalledInternalLoadingState.Error(
                        state.throwable
                    )
                )

                is FapInstalledFromNetworkState.LoadedOffline -> flowOf(
                    FapInstalledInternalLoadingState.LoadedOffline(
                        state.faps
                    )
                )

                FapInstalledFromNetworkState.Loading -> flowOf(FapInstalledInternalLoadingState.Loading)

                is FapInstalledFromNetworkState.Loaded -> getLoadedFapsFlow(
                    fapItems = state.faps.map { it.second }
                )
            }
        }
    }

    private fun getLoadedFapsFlow(
        fapItems: List<FapItemShort>
    ): Flow<FapInstalledInternalLoadingState.Loaded> {
        if (fapItems.isEmpty()) {
            return flowOf(FapInstalledInternalLoadingState.Loaded(persistentListOf()))
        }

        info { "Update #getLoadedFapsFlow. ${fapItems.size} fap items" }
        val flows = fapItems.map { fapItem ->
            fapStateManager.getFapStateFlow(fapItem.id, fapItem.upToDateVersion)
                .map { fapItem to it }
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

    private suspend fun updateStateUnsafe(
        uids: ImmutableList<String>,
        flipperTarget: FlipperTarget
    ): FapInstalledFromNetworkState {
        val toRequestItems = uids.toMutableSet()
        val alreadyLoadedApps = when (val currentState = applicationFromNetworkStateFlow.value) {
            is FapInstalledFromNetworkState.Error,
            is FapInstalledFromNetworkState.LoadedOffline,
            FapInstalledFromNetworkState.Loading -> persistentListOf()

            is FapInstalledFromNetworkState.Loaded -> currentState.faps
        }.filter { (fapTarget, _) -> fapTarget == flipperTarget }
            .filter { (_, fapItem) -> toRequestItems.contains(fapItem.id) }

        alreadyLoadedApps.forEach { (_, fapItem) ->
            toRequestItems.remove(fapItem.id)
        }
        info { "To request apps: $toRequestItems, Loaded apps: ${alreadyLoadedApps.map { it.second.id }}" }

        val isUiThread = Looper.myLooper() == Looper.getMainLooper()

        info { "isUiThread: $isUiThread" }

        if (toRequestItems.isEmpty()) {
            info { "Not found faps for download" }
            return FapInstalledFromNetworkState.Loaded(alreadyLoadedApps.toImmutableList())
        }

        val faps = fapNetworkApi.getAllItem(
            applicationIds = toRequestItems.toList(),
            offset = 0,
            limit = toRequestItems.size,
            sortType = SortType.UPDATE_AT_DESC,
            target = flipperTarget
        ).getOrThrow()

        info { "Loaded ${faps.size} faps from network" }

        return FapInstalledFromNetworkState.Loaded(
            alreadyLoadedApps.map { (_, fapItem) -> fapItem }
                .plus(faps)
                .map { flipperTarget to it }
                .toImmutableList()
        )
    }
}

private sealed class FapInstalledFromNetworkState {
    object Loading : FapInstalledFromNetworkState()

    data class LoadedOffline(
        val faps: ImmutableList<OfflineFapApp>
    ) : FapInstalledFromNetworkState()

    data class Loaded(
        val faps: ImmutableList<Pair<FlipperTarget, FapItemShort>>
    ) : FapInstalledFromNetworkState()

    data class Error(
        val throwable: Throwable
    ) : FapInstalledFromNetworkState()
}
