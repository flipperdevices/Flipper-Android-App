package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.connection.feature.protocolversion.api.FVersionFeatureApi
import com.flipperdevices.bridge.connection.feature.provider.api.FFeatureProvider
import com.flipperdevices.bridge.connection.feature.provider.api.getSync
import com.flipperdevices.bridge.connection.feature.storage.api.FStorageFeatureApi
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
        val fStorageFeatureApi = fFeatureProvider.getSync<FStorageFeatureApi>()
        if (fStorageFeatureApi == null) {
            error { "#atomicMove could not find FStorageFeatureApi" }
            return
        }
        fromToPair.mapNotNull { (_, to) -> File(to).parent }
            .forEach { fullPath ->
                fStorageFeatureApi.uploadApi().mkdir(fullPath)
                    .onFailure { error(it) { "#atomicMove could not mkdir($fullPath)" } }
            }

        withContext(NonCancellable) {
            val fSemVer = fFeatureProvider.getSync<FVersionFeatureApi>()
                ?.getVersionInformationFlow()
                ?.first()
            if (fSemVer == null || fSemVer < UNIX_MV_SUPPORTED_VERSION_API) {
                fromToPair
                    .map { pair -> pair.second }
                    .map { target ->
                        async {
                            fStorageFeatureApi.deleteApi().delete(target)
                                .onFailure { error(it) { "#atomicMove could not delete $target" } }
                        }
                    }
                    .awaitAll()
            }

            fromToPair.map { (from, to) ->
                fStorageFeatureApi.uploadApi().move(
                    oldPath = from.toPath(),
                    newPath = to.toPath()
                )
                    .onFailure { error(it) { "#atomicMove could not move $from -> $to" } }
                    .getOrThrow()
            }
        }
    }
}
