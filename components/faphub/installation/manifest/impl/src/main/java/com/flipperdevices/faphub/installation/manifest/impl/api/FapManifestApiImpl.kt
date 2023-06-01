package com.flipperdevices.faphub.installation.manifest.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import com.flipperdevices.faphub.dao.api.FapVersionApi
import com.flipperdevices.faphub.installation.manifest.api.FapManifestApi
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestDeleter
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestUploader
import com.flipperdevices.faphub.installation.manifest.impl.utils.FapManifestsLoader
import com.flipperdevices.faphub.installation.manifest.model.FapManifestItem
import com.flipperdevices.faphub.installation.manifest.model.FapManifestVersion
import com.squareup.anvil.annotations.ContributesBinding
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex

@Singleton
@ContributesBinding(AppGraph::class, FapManifestApi::class)
class FapManifestApiImpl @Inject constructor(
    private val loader: FapManifestsLoader,
    private val manifestUploader: FapManifestUploader,
    private val fapVersionApi: FapVersionApi,
    private val manifestDeleter: FapManifestDeleter
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
        fapManifestItemFlow.update { manifests ->
            if (manifests != null) {
                val toDelete =
                    manifests.filter { it.applicationAlias == fapManifestItem.applicationAlias }
                manifests.minus(toDelete.toSet()).plus(fapManifestItem)
            } else {
                null
            }
        }
    }

    override suspend fun remove(
        applicationId: String
    ) = withLock(mutex, "remove") {
        val toRemoveManifest = fapManifestItemFlow.value?.find {
            it.uid == applicationId
        }
        if (toRemoveManifest == null) {
            warn { "Can't find manifest for $applicationId" }
            return@withLock
        }
        info { "Delete for $applicationId $toRemoveManifest" }
        manifestDeleter.delete(toRemoveManifest)
        fapManifestItemFlow.update { manifests ->
            manifests?.minus(toRemoveManifest)
        }
    }

    override fun invalidateAsync() = launchWithLock(mutex, scope, "invalidate") {
        fapManifestItemFlow.emit(null)
        runCatching {
            loader.load()
        }.mapCatching { manifestItems ->
            val versions = fapVersionApi.getVersions(manifestItems.map { it.versionUid })
                .associateBy { it.id }
            manifestItems.mapNotNull { internalManifestItem ->
                val version = versions[internalManifestItem.versionUid] ?: return@mapNotNull null
                FapManifestItem(
                    applicationAlias = internalManifestItem.applicationAlias,
                    uid = internalManifestItem.uid,
                    version = FapManifestVersion(
                        versionUid = version.id,
                        semVer = version.version
                    ),
                    path = internalManifestItem.path
                )
            }
        }.onFailure {
            error(it) { "Failed load manifests" }
            shouldInvalidate.compareAndSet(false, true)
        }.onSuccess {
            fapManifestItemFlow.emit(it)
        }
    }
}
