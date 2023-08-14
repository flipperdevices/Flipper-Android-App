package com.flipperdevices.faphub.installation.manifest.impl.api

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.ktx.jre.withLockResult
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.dao.api.FapVersionApi
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestCacheLoader
import com.flipperdevices.faphub.installation.manifest.model.FapManifestEnrichedItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex

class FapManifestEnrichedHelper(
    private val scope: CoroutineScope,
    private val versionApi: FapVersionApi,
    application: Application,
    private val cacheLoader: FapManifestCacheLoader
) : LogTagProvider, ConnectivityManager.NetworkCallback() {
    override val TAG = "FapManifestEnrichedHelper"

    private val mutex = Mutex()
    private val fapManifestState = MutableStateFlow<FapManifestState>(FapManifestState.Loading)

    init {
        try {
            application.getSystemService(
                ConnectivityManager::class.java
            ).registerDefaultNetworkCallback(
                this@FapManifestEnrichedHelper
            )
        } catch (ex: Exception) {
            error(ex) { "Failed register network callback" }
        }
        fapManifestState.onEach { state ->
            when (state) {
                is FapManifestState.Loaded -> cacheLoader.invalidate(state.items.map { it.fapManifestItem })
                is FapManifestState.LoadedOffline -> cacheLoader.invalidate(state.items)
                FapManifestState.Loading,
                is FapManifestState.NotLoaded -> {
                }
            }
        }.launchIn(scope)
    }

    fun getManifestState() = fapManifestState.asStateFlow()

    suspend fun onLoadFresh() = withLock(mutex, "load_fresh") {
        fapManifestState.emit(FapManifestState.Loading)
    }

    suspend fun onUpdateManifests(
        manifests: List<FapManifestItem>
    ) = withLock(mutex, "new_manifest") {
        info { "Update manifest with ${manifests.size} manifests" }
        invalidateInternal(manifests)
    }

    suspend fun onAdd(
        fapManifestEnrichedItem: FapManifestEnrichedItem
    ) = withLock(mutex, "on_add") {
        when (val manifestState = fapManifestState.value) {
            FapManifestState.Loading,
            is FapManifestState.NotLoaded -> return@withLock

            is FapManifestState.LoadedOffline -> {
                onUpdateManifests(
                    manifestState.items
                        .filterNot { it.uid == fapManifestEnrichedItem.fapManifestItem.uid }
                        .plus(fapManifestEnrichedItem.fapManifestItem)
                )
                return@withLock
            }

            is FapManifestState.Loaded -> {
                fapManifestState.emit(
                    FapManifestState.Loaded(
                        manifestState.items
                            .filterNot { it.fapManifestItem.uid == fapManifestEnrichedItem.fapManifestItem.uid }
                            .plus(
                                fapManifestEnrichedItem
                            ).toImmutableList()
                    )
                )
            }
        }
    }

    suspend fun onDelete(
        applicationUid: String
    ): List<FapManifestItem> = withLockResult(mutex, "on_delete") {
        when (val manifestState = fapManifestState.value) {
            FapManifestState.Loading,
            is FapManifestState.NotLoaded -> return@withLockResult emptyList()

            is FapManifestState.Loaded -> {
                val itemsToDelete = manifestState.items.filter {
                    it.fapManifestItem.uid == applicationUid
                }.toSet()
                fapManifestState.emit(
                    FapManifestState.Loaded(
                        manifestState.items.minus(itemsToDelete).toImmutableList()
                    )
                )
                return@withLockResult itemsToDelete.map { it.fapManifestItem }
            }

            is FapManifestState.LoadedOffline -> {
                val itemsToDelete = manifestState.items.filter {
                    it.uid == applicationUid
                }
                fapManifestState.emit(
                    FapManifestState.LoadedOffline(
                        manifestState.items.minus(itemsToDelete.toSet()).toImmutableList()
                    )
                )
                return@withLockResult itemsToDelete
            }
        }
    }

    suspend fun onError(throwable: Throwable) = withLock(mutex, "on_error") {
        error(throwable) { "Receive error" }
        fapManifestState.emit(FapManifestState.NotLoaded(throwable))
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        launchWithLock(mutex, scope, "internet_available") {
            val manifestState = fapManifestState.value
            if (manifestState is FapManifestState.LoadedOffline) {
                invalidateInternal(manifestState.items)
            }
        }
    }

    private suspend fun invalidateInternal(manifests: List<FapManifestItem>) {
        runCatching {
            if (manifests.isEmpty()) {
                return@runCatching emptyList()
            }
            val numberVersions = versionApi.getVersionsMap(manifests.map { it.versionUid })
            info { "Loaded $numberVersions" }

            val loadedManifests = manifests.mapNotNull {
                FapManifestEnrichedItem(
                    fapManifestItem = it,
                    numberVersion = numberVersions[it.versionUid] ?: return@mapNotNull null
                )
            }
            info {
                "Loaded ${loadedManifests.size}/${manifests.size}. Not loaded: ${
                    manifests.filter {
                        !numberVersions.containsKey(
                            it.versionUid
                        )
                    }.map { it.versionUid }
                }. Loaded $loadedManifests"
            }
            return@runCatching loadedManifests
        }.onSuccess {
            fapManifestState.emit(FapManifestState.Loaded(it.toImmutableList()))
        }.onFailure {
            fapManifestState.emit(FapManifestState.LoadedOffline(manifests.toImmutableList()))
            error(it) { "Failed load manifest versions" }
        }
    }
}
