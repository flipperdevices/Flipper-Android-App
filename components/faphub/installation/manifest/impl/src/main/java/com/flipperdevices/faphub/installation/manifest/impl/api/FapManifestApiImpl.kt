package com.flipperdevices.faphub.installation.manifest.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestDeleter
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestUploader
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestsLoader
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestState
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FapManifestApi::class)
class FapManifestApiImpl @Inject constructor(
    loaderFactory: FapManifestsLoader.Factory,
    private val manifestUploader: FapManifestUploader,
    private val manifestDeleter: FapManifestDeleter,
) : FapManifestApi, LogTagProvider {
    override val TAG = "FapManifestApi"

    private val scope = CoroutineScope(SupervisorJob() + FlipperDispatchers.workStealingDispatcher)
    private val loader = loaderFactory(scope)

    private val fapManifestStateFlow = MutableStateFlow<FapManifestState>(
        FapManifestState.Loaded(
            items = persistentListOf(),
            inProgress = true
        )
    )

    private val mutex = Mutex()

    private var job: Job? = null
    private val jobInvalidateMutex = Mutex()

    init {
        invalidateAsync()
    }

    override fun getManifestFlow() = fapManifestStateFlow.asStateFlow()

    override suspend fun add(
        pathToFap: String,
        fapManifestItem: FapManifestItem
    ) = withLock(mutex, "add") {
        info { "Add $pathToFap to $fapManifestItem " }

        manifestUploader.save(pathToFap, fapManifestItem)
        fapManifestStateFlow.update { fapManifestState ->
            if (fapManifestState is FapManifestState.Loaded) {
                fapManifestState.copy(
                    items = fapManifestState
                        .items
                        .filter { it.uid != fapManifestItem.uid }
                        .plus(fapManifestItem)
                        .toPersistentList()
                )
            } else {
                fapManifestState
            }
        }
    }

    override suspend fun remove(
        applicationUid: String
    ) = withLock(mutex, "remove") {
        val toRemoveManifests = (fapManifestStateFlow.value as? FapManifestState.Loaded)
            ?.items
            ?.filter { it.uid == applicationUid }
        info { "On $applicationUid toRemoveManifests is $toRemoveManifests" }

        if (toRemoveManifests.isNullOrEmpty()) {
            return@withLock
        }

        fapManifestStateFlow.update { fapManifestState ->
            if (fapManifestState is FapManifestState.Loaded) {
                fapManifestState.copy(
                    items = fapManifestState
                        .items
                        .minus(toRemoveManifests)
                        .toPersistentList()
                )
            } else {
                fapManifestState
            }
        }
        toRemoveManifests.forEach {
            manifestDeleter.delete(it)
        }
    }

    override fun invalidateAsync() = launchWithLock(jobInvalidateMutex, scope) {
        val oldJob = job
        job = scope.launch {
            oldJob?.cancelAndJoin()
            loader.invalidate()
            loader.getManifestLoaderState()
                .collectLatest { manifestLoaderState ->
                    withLock(mutex, "invalidate") {
                        fapManifestStateFlow.emit(manifestLoaderState.toManifestState())
                    }
                }
        }
    }
}
