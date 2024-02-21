package com.flipperdevices.faphub.installation.manifest.impl.utils

import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi
import com.flipperdevices.bridge.api.model.FlipperRequest
import com.flipperdevices.bridge.api.model.wrapToRequest
import com.flipperdevices.bridge.rpc.api.FlipperStorageApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.data.SemVer
import com.flipperdevices.protobuf.Flipper
import com.flipperdevices.protobuf.main
import com.flipperdevices.protobuf.storage.deleteRequest
import com.flipperdevices.protobuf.storage.renameRequest
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

private val UNIX_MV_SUPPORTED_VERSION_API = SemVer(majorVersion = 0, minorVersion = 17)

class FapManifestAtomicMover @Inject constructor(
    private val flipperServiceProvider: FlipperServiceProvider,
    private val flipperStorageApi: FlipperStorageApi
) {
    suspend fun atomicMove(
        vararg fromToPair: Pair<String, String>
    ) {
        val serviceApi = flipperServiceProvider.getServiceApi()
        fromToPair.mapNotNull { (_, to) -> File(to).parent }.forEach {
            flipperStorageApi.mkdirs(it)
        }
        val preparedRequest = getPrepareRequests(
            serviceApi.flipperVersionApi,
            fromToPair.map { it.second }
        )
        val moveRequests = getRequests(fromToPair)

        withContext(NonCancellable) {
            @Suppress("SpreadOperator")
            serviceApi.requestApi.requestWithoutAnswer(*preparedRequest.toTypedArray())
            moveRequests.map {
                serviceApi.requestApi.request(it)
            }.map { it.first() }.forEach { response ->
                if (response.commandStatus != Flipper.CommandStatus.OK) {
                    error("Failed move path, failed response: $response")
                }
            }
        }
    }

    private suspend fun getPrepareRequests(
        versionApi: FlipperVersionApi,
        targets: List<String>
    ): List<FlipperRequest> {
        val version = versionApi.getVersionInformationFlow().first()
        if (version != null && version >= UNIX_MV_SUPPORTED_VERSION_API) {
            return emptyList()
        }
        return targets.map { target ->
            main {
                storageDeleteRequest = deleteRequest {
                    path = target
                }
            }.wrapToRequest()
        }
    }

    private fun getRequests(
        fromToPair: Array<out Pair<String, String>>
    ) = fromToPair.map { (from, to) ->
        main {
            storageRenameRequest = renameRequest {
                oldPath = from
                newPath = to
            }
        }.wrapToRequest()
    }
}
