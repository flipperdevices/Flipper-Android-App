package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okio.Path.Companion.toPath
import java.io.File
import javax.inject.Inject

private val UNIX_MV_SUPPORTED_VERSION_API = SemVer(majorVersion = 0, minorVersion = 17)

class FapManifestAtomicMover @Inject constructor(
    private val fFeatureProvider: FFeatureProvider
) : LogTagProvider {
    override val TAG: String = "FapManifestAtomicMover"
    suspend fun atomicMove(
        vararg fromToPair: Pair<String, String>
    ) {
        val fSemVer = fFeatureProvider.getSync<FVersionFeatureApi>()
            ?.getVersionInformationFlow()
            ?.first()

        val fStorageFeatureApi = fFeatureProvider.getSync<FStorageFeatureApi>()
        if (fStorageFeatureApi == null) {
            error { "#atomicMove could not find FStorageFeatureApi" }
            return
        }
        fromToPair.mapNotNull { (_, to) -> File(to).parent }
            .forEach { fullPath -> fStorageFeatureApi.uploadApi().mkdir(fullPath) }

        val deleteTargets = fromToPair.map { pair -> pair.second }

        withContext(NonCancellable) {
            if (fSemVer == null || fSemVer < UNIX_MV_SUPPORTED_VERSION_API) {
                deleteTargets.map { target ->
                    fStorageFeatureApi.deleteApi().delete(target)
                }
            }

            fromToPair.map { (from, to) ->
                fStorageFeatureApi.uploadApi().move(
                    oldPath = from.toPath(),
                    newPath = to.toPath()
                )
            }.onEach { result ->
                result
                    .onFailure { error(it) { "Failed move path" } }
                    .getOrThrow()
            }
        }
    }
}
