package com.flipperdevices.faphub.installation.manifest.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestUploader
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestsLoader
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FapManifestApi::class)
class FapManifestApiImpl @Inject constructor(
    private val loader: FapManifestsLoader,
    private val manifestUploader: FapManifestUploader,
) : FapManifestApi, LogTagProvider {
    override val TAG = "FapManifestApi"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private val fapManifestItemFlow = MutableStateFlow<List<FapManifestItem>?>(null)
    private val shouldInvalidate = AtomicBoolean(true)

    override fun getManifestFlow(): StateFlow<List<FapManifestItem>?> {
        if (shouldInvalidate.compareAndSet(true, false)) {
            invalidateAsync()
        }
        return fapManifestItemFlow.asStateFlow()
    }

    override suspend fun add(
        pathToFap: String,
        fapManifestItem: FapManifestItem
    ) = withLock(mutex, "add") {
        manifestUploader.save(pathToFap, fapManifestItem)
        fapManifestItemFlow.update {
            it?.plus(fapManifestItem) ?: it
        }
    }

    private fun invalidateAsync() = launchWithLock(mutex, scope, "invalidate") {
        runCatching {
            loader.load()
        }.onFailure {
            error(it) { "Failed load manifests" }
            shouldInvalidate.compareAndSet(false, true)
        }.onSuccess {
            fapManifestItemFlow.emit(it)
        }
    }
}
