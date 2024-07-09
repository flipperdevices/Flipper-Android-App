package com.flipperdevices.faphub.installedtab.impl.viewmodel

import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.FapNetworkApi
import com.flipperdevices.faphub.dao.api.model.SortType
import com.flipperdevices.faphub.installation.stateprovider.api.api.FapInstallationStateManager
import com.flipperdevices.faphub.installation.stateprovider.api.model.FapState
import com.flipperdevices.faphub.installation.stateprovider.api.model.NotAvailableReason
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.installedtab.impl.model.InstalledFapApp
import com.flipperdevices.faphub.installedtab.impl.model.InstalledNetworkErrorEnum
import com.flipperdevices.faphub.installedtab.impl.model.toInstalledNetworkErrorEnum
import com.flipperdevices.faphub.target.api.FlipperTargetProviderApi
import com.flipperdevices.faphub.target.model.FlipperTarget
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import javax.inject.Singleton

@Singleton
class InstalledFapsFromNetworkProducer @Inject constructor(
    private val installedFapsUidsProducer: InstalledFapsUidsProducer,
    private val flipperTargetProviderApi: FlipperTargetProviderApi,
    private val fapNetworkApi: FapNetworkApi,
    private val fapStateManager: FapInstallationStateManager,
    private val globalScope: CoroutineScope
) : LogTagProvider {
    override val TAG = "InstalledFapsFromNetworkProducer-${hashCode()}"

    private val applicationFromNetworkStateFlow = MutableStateFlow<FapInstalledFromNetworkState>(
        FapInstalledFromNetworkState.Loaded(
            faps = persistentListOf(),
            inProgress = true
        )
    )
    private var fetchFromNetworkJob: Job? = null
    private val mutex = Mutex()

    fun refresh(force: Boolean) {
        info { "Call refresh $force" }
        installedFapsUidsProducer.refresh(globalScope, force)
        val oldJob = fetchFromNetworkJob
        fetchFromNetworkJob = globalScope.launch(FlipperDispatchers.workStealingDispatcher) {
            oldJob?.cancelAndJoin()
            if (force) {
                applicationFromNetworkStateFlow.emit(
                    FapInstalledFromNetworkState.Loaded(
                        faps = persistentListOf(),
                        inProgress = true
                    )
                )
            }
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

                        is FapInstalledUidsState.Loaded -> try {
                            updateStateUnsafe(uidsState, flipperTarget)
                        } catch (throwable: Throwable) {
                            error(throwable) { "Failed load faps" }
                            FapInstalledFromNetworkState.Error(throwable)
                        }
                    }
                    info { "Update applicationFromNetworkStateFlow with state $state" }
                    applicationFromNetworkStateFlow.emit(state)
                }
            }
        }
    }

    private suspend fun updateStateUnsafe(
        installedState: FapInstalledUidsState.Loaded,
        flipperTarget: FlipperTarget
    ): FapInstalledFromNetworkState {
        val toRequestItems = installedState.faps.associateBy { it.applicationUid }.toMutableMap()
        val currentLoadedItems = when (val currentState = applicationFromNetworkStateFlow.value) {
            is FapInstalledFromNetworkState.Error -> persistentListOf()
            is FapInstalledFromNetworkState.Loaded -> currentState.faps
        }

        info { "currentLoadedItems: ${currentLoadedItems.map { it.second.applicationUid }}" }
        val alreadyLoadedApps = currentLoadedItems
            .filter { (fapTarget, _) -> fapTarget == flipperTarget }
            .filter { (_, fapItem) -> toRequestItems.contains(fapItem.applicationUid) }
            .filter { (_, fapItem) -> fapItem is InstalledFapApp.OnlineFapApp }

        alreadyLoadedApps.forEach { (_, fapItem) ->
            toRequestItems.remove(fapItem.applicationUid)
        }
        info { "To request apps: $toRequestItems, Loaded apps: ${alreadyLoadedApps.map { it.second.applicationUid }}" }

        if (toRequestItems.isEmpty()) {
            info { "Not found faps for download" }
            return FapInstalledFromNetworkState.Loaded(
                faps = alreadyLoadedApps.toImmutableList(),
                inProgress = installedState.inProgress,
                networkError = null
            )
        }

        val loadedFapsResult = fapNetworkApi.getAllItem(
            applicationIds = toRequestItems.keys.toList(),
            offset = 0,
            limit = toRequestItems.size,
            sortType = SortType.UPDATE_AT_DESC,
            target = flipperTarget
        )
        val loadedFaps = loadedFapsResult.getOrElse { emptyList() }
        info { "Loaded ${loadedFaps.size} faps from network" }
        val networkError = loadedFapsResult.exceptionOrNull()
        val readyFaps = mutableListOf<InstalledFapApp>()
        installedState.faps.forEach { installedFap ->
            val fapFromNetwork = loadedFaps.find { it.id == installedFap.applicationUid }
            val alreadyLoadedFap = currentLoadedItems.find {
                it.second.applicationUid == installedFap.applicationUid
            }
            if (fapFromNetwork != null) { // If app exist in network response
                readyFaps.add(InstalledFapApp.OnlineFapApp(fapFromNetwork))
            } else if (alreadyLoadedFap != null) { // If app already loaded
                readyFaps.add(alreadyLoadedFap.second)
            } else {
                when (installedFap) {
                    is FapInstalledFromManifest.Offline -> readyFaps.add(installedFap.offlineFap)
                    is FapInstalledFromManifest.RawUid -> {}
                }
            }
        }

        return FapInstalledFromNetworkState.Loaded(
            faps = readyFaps
                .map { flipperTarget to it }
                .toImmutableList(),
            inProgress = installedState.inProgress,
            networkError = networkError?.toInstalledNetworkErrorEnum()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLoadedFapsFlow(): Flow<FapInstalledInternalLoadingState> {
        return applicationFromNetworkStateFlow.flatMapLatest { state ->
            return@flatMapLatest when (state) {
                is FapInstalledFromNetworkState.Error -> flowOf(
                    FapInstalledInternalLoadingState.Error(
                        state.throwable
                    )
                )

                is FapInstalledFromNetworkState.Loaded -> getLoadedFapsFlow(state)
            }
        }
    }

    private fun getLoadedFapsFlow(
        fapState: FapInstalledFromNetworkState.Loaded
    ): Flow<FapInstalledInternalLoadingState.Loaded> {
        if (fapState.faps.isEmpty()) {
            return flowOf(
                FapInstalledInternalLoadingState.Loaded(
                    faps = persistentListOf(),
                    inProgress = fapState.inProgress
                )
            )
        }

        info { "Update #getLoadedFapsFlow. ${fapState.faps.size} fap items" }
        val flows = fapState.faps.map { (_, fapItem) ->
            when (fapItem) {
                is InstalledFapApp.OfflineFapApp -> flowOf(
                    FapState.NotAvailableForInstall(
                        NotAvailableReason.NOT_AVAILABLE_ONLINE
                    )
                )

                is InstalledFapApp.OnlineFapApp -> fapStateManager.getFapStateFlow(
                    fapItem.applicationUid,
                    fapItem.fapItemShort.upToDateVersion
                )
            }.map { fapItem to it }
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

                    is FapState.NotAvailableForInstall -> if (state.reason == NotAvailableReason.NOT_AVAILABLE_ONLINE) {
                        FapInstalledInternalState.InstalledOffline
                    } else {
                        FapInstalledInternalState.Installed
                    }

                    else -> FapInstalledInternalState.Installed
                }
            }
        }.map {
            FapInstalledInternalLoadingState.Loaded(
                faps = it.toImmutableList(),
                inProgress = fapState.inProgress,
                networkError = fapState.networkError
            )
        }
    }
}

private sealed class FapInstalledFromNetworkState {
    data class Loaded(
        val faps: ImmutableList<Pair<FlipperTarget, InstalledFapApp>>,
        val inProgress: Boolean,
        val networkError: InstalledNetworkErrorEnum? = null
    ) : FapInstalledFromNetworkState()

    data class Error(
        val throwable: Throwable
    ) : FapInstalledFromNetworkState()
}
